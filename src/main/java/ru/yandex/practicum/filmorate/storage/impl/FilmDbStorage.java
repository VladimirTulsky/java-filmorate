package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private final MpaDbStorage mpaDbStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate, MpaDbStorage mpaDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.mpaDbStorage = mpaDbStorage;
    }

    @Override
    public Collection<Film> findAll() {
        String sql = "SELECT films.*, m.* " +
                "FROM films " +
                "JOIN mpa m ON m.MPA_ID = films.mpa_id";
        List<Film> films = jdbcTemplate.query(sql, this::makeFilm);
        findGenresByList(films);

        return films;
    }

    @Override
    public Film create(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        film.setMpa(mpaDbStorage.getById(film.getMpa().getId()).orElseThrow());
        String genresSql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        if (film.getGenres() != null) {
            film.getGenres().stream()
                    .map(g -> jdbcTemplate.update(genresSql, film.getId(), g.getId()))
                    .collect(Collectors.toList());
            film.setGenres(findGenresById(film.getId()));
        }

        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
                "duration = ?, mpa_id = ?" +
                "WHERE FILM_ID = ?";
        film.setMpa(mpaDbStorage.getById(film.getMpa().getId()).orElseThrow());
        if (film.getGenres() != null) {
            String deleteGenres = "DELETE FROM film_genre WHERE film_id = ?";
            String updateGenres = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            jdbcTemplate.update(deleteGenres, film.getId());
            for (Genre g : film.getGenres()) {
                String checkDuplicate = "SELECT * FROM film_genre WHERE film_id = ? AND genre_id = ?";
                SqlRowSet checkRows = jdbcTemplate.queryForRowSet(checkDuplicate, film.getId(), g.getId());
                if (!checkRows.next()) {
                    jdbcTemplate.update(updateGenres, film.getId(), g.getId());
                }
                film.setGenres(findGenresById(film.getId()));
            }
        }
        jdbcTemplate.update(sql,
                film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());

        return film;
    }

    @Override
    public Optional<Film> getById(int id) {
        String sql = "SELECT films.*, m.* " +
                "FROM films " +
                "JOIN mpa m ON m.MPA_ID = films.mpa_id " +
                "WHERE films.film_id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, id);
        if (!filmRows.next()) {
            return Optional.empty();
        }
        Film film = jdbcTemplate.queryForObject(sql, this::makeFilm, id);
        Objects.requireNonNull(film).setGenres(findGenresById(film.getId()));

        return Optional.of(film);
    }

    @Override
    public Optional<Film> deleteById(int id) {
        Optional<Film> film = getById(id);
        String genresSql = "DELETE FROM film_genre WHERE film_id = ?";
        String sql = "DELETE FROM films WHERE FILM_ID = ?";
        jdbcTemplate.update(genresSql, id);
        jdbcTemplate.update(sql, id);

        return film;
    }

    @Override
    public Optional<Film> addLike(int filmId, int userId) {
        String sql = "INSERT INTO films_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);

        return getById(filmId);
    }

    @Override
    public Optional<Film> removeLike(int filmId, int userId) {
        String sql = "DELETE FROM films_likes " +
                "WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);

        return getById(filmId);
    }

    @Override
    public List<Film> getBestFilms(int count) {
        String sql = "SELECT films.FILM_ID, films.name, description, release_date, duration, m.mpa_id, m.name " +
                "FROM films " +
                "LEFT JOIN films_likes fl ON films.FILM_ID = fl.film_id " +
                "LEFT JOIN mpa m on m.MPA_ID = films.mpa_id " +
                "GROUP BY films.FILM_ID, fl.film_id IN ( " +
                "SELECT film_id " +
                "FROM films_likes " +
                ") " +
                "ORDER BY COUNT(fl.film_id) DESC " +
                "LIMIT ?";
        List<Film> films = jdbcTemplate.query(sql, this::makeFilm, count);
        findGenresByList(films);

        return films;
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        long duration = rs.getLong("duration");
        Mpa mpa = new Mpa(rs.getInt("mpa.mpa_id"), rs.getString("mpa.name"));

        return new Film(id, name, description, releaseDate, duration, mpa, new ArrayList<>());
    }

    private List<Genre> findGenresById(int filmId) {
        String genresSql = "SELECT genre.genre_id, name " +
                "FROM genre " +
                "LEFT JOIN FILM_GENRE FG on genre.genre_id = FG.GENRE_ID " +
                "WHERE film_id = ?";

        return jdbcTemplate.query(genresSql, GenreDbStorage::makeGenre, filmId);
    }

    private void findGenresByList(List<Film> films) {
        String sqlGenres = "SELECT film_id, g2.* " +
                "FROM FILM_GENRE " +
                "JOIN genre g2 ON g2.genre_id = film_genre.genre_id " +
                "WHERE film_id IN (:ids)";
        List<Integer> ids = films.stream()
                .map(Film::getId).collect(Collectors.toList());
        Map<Integer, Film> filmMap = films.stream()
                .collect(Collectors.toMap(Film::getId, film -> film, (a, b) -> b));
        SqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        SqlRowSet sqlRowSet = namedJdbcTemplate.queryForRowSet(sqlGenres, parameters);
        while (sqlRowSet.next()) {
            int filmId = sqlRowSet.getInt("film_id");
            int genreId = sqlRowSet.getInt("genre_id");
            String name = sqlRowSet.getString("name");
            filmMap.get(filmId).getGenres().add(new Genre(genreId, name));
        }
        films.stream()
                .map(film -> film.getGenres().addAll(filmMap.get(film.getId()).getGenres()));
    }
}
