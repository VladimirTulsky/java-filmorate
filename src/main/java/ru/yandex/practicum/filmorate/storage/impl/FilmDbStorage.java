package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DataException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    @Override
    public Collection<Film> findAll() {
        String sql = "SELECT films.*, m.* " +
                "FROM films " +
                "JOIN mpa m ON m.MPA_ID = films.mpa_id";
        List<Film> films = jdbcTemplate.query(sql, FilmDbStorage::makeFilm);
        loadGenres(films);

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
        addGenres(film);

        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
                "duration = ?, mpa_id = ?" +
                "WHERE FILM_ID = ?";
        deleteGenres(film);
        addGenres(film);
        int result = jdbcTemplate.update(sql,
                film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());
        if (result < 1) throw new DataException("Фильм не найден в базе");

        return film;
    }

    @Override
    public Optional<Film> getById(int id) {
        String sql = "SELECT films.*, m.* " +
                "FROM films " +
                "JOIN mpa m ON m.MPA_ID = films.mpa_id " +
                "WHERE films.film_id = ?";
        try {
            Film film = jdbcTemplate.queryForObject(sql, FilmDbStorage::makeFilm, id);
            loadGenres(Collections.singletonList(film));
            return Optional.ofNullable(film);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
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
        String sql = "MERGE INTO films_likes (film_id, user_id) VALUES (?, ?)";
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
        List<Film> films = jdbcTemplate.query(sql, FilmDbStorage::makeFilm, count);
        loadGenres(films);

        return films;
    }

    @Override
    public List<Film> searchUsingKeyWord(String query, String by) {
        List<Film> films;
        String director = null;
        String name = null;
        String sqlForAll = "select f.*, m.* " +
                "from films as f " +
                "join mpa as m ON m.mpa_id = f.mpa_id " +
                "join film_director as fd on fd.film_id = f.film_id " +
                "join directors as d on d.director_id = fd.director_id " +
                "where f.name = ? or f.description = ? or d.name = ?";
        if (by != null) {
            String[] splitter = by.split(",");
            if (splitter[0] == null) {
                director = null;
            } else {
                director = splitter[0];
            }
            if (splitter.length != 2) {
                name = null;
            } else {
                name = splitter[1];
            }
        } else {
            throw new ObjectNotFoundException("Ключевое слово не найдено.");
        }
        if (director == null && name == null) {
            String sqlIfNull = "SELECT f.*, m.* FROM films as f JOIN mpa m ON m.mpa_id = f.mpa_id WHERE f.name = ? OR f.description = ?";
            films = jdbcTemplate.query(sqlIfNull, FilmDbStorage::makeFilm, query, query);
        } else if (director != null) {
            films = jdbcTemplate.query(sqlForAll, FilmDbStorage::makeFilm, query, query, director);
        } else if (name != null) {
            films = jdbcTemplate.query(sqlForAll, FilmDbStorage::makeFilm, name, query, query);
        } else {
            films = jdbcTemplate.query(sqlForAll, FilmDbStorage::makeFilm, name, query, director);
        }
        return films;
    }

    static Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        long duration = rs.getLong("duration");
        Mpa mpa = new Mpa(rs.getInt("mpa.mpa_id"), rs.getString("mpa.name"));

        return new Film(id, name, description, releaseDate, duration, mpa, new LinkedHashSet<>());
    }

    private void loadGenres(List<Film> films) {
        String sqlGenres = "SELECT film_id, g2.* " +
                "FROM FILM_GENRE " +
                "JOIN genre g2 ON g2.genre_id = film_genre.genre_id " +
                "WHERE film_id IN (:ids)";
        List<Integer> ids = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());
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
        films.forEach(film -> film.getGenres().addAll(filmMap.get(film.getId()).getGenres()));
    }

    private void addGenres(Film film) {
        if (film.getGenres() != null) {
            String updateGenres = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            jdbcTemplate.batchUpdate(
                    updateGenres, film.getGenres(), film.getGenres().size(),
                    (ps, genre) -> {
                        ps.setInt(1, film.getId());
                        ps.setInt(2, genre.getId());
                    });
        } else film.setGenres(new LinkedHashSet<>());
    }

    private void deleteGenres(Film film) {
        String deleteGenres = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(deleteGenres, film.getId());
    }
}