package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DataException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> findAll() {
        String sql = "SELECT films.*, m.* " +
                "FROM films " +
                "JOIN mpa m ON m.MPA_ID = films.mpa_id";

        return jdbcTemplate.query(sql, FilmDbStorage::makeFilm);
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
        addDirectors(film);

        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
                "duration = ?, mpa_id = ?" +
                "WHERE FILM_ID = ?";
        deleteGenres(film);
        deleteDirectors(film);
        addGenres(film);
        addDirectors(film);
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
            return Optional.ofNullable(film);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Film> deleteById(int id) {
        Optional<Film> film = getById(id);
        String sql = "DELETE FROM films WHERE FILM_ID = ?";
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

        return jdbcTemplate.query(sql, FilmDbStorage::makeFilm, count);
    }

    @Override
    public List<Film> getAllByDirector(int directorId, String sortBy) {
        String sortedByLikes = "SELECT f.*, M.*, FD.DIRECTOR_ID " +
                "FROM FILM_DIRECTOR FD " +
                "JOIN FILMS F on F.FILM_ID = FD.FILM_ID " +
                "JOIN MPA M on F.mpa_id = M.MPA_ID " +
                "LEFT JOIN films_likes fl on F.film_id = fl.film_id " +
                "WHERE DIRECTOR_ID = ? " +
                "GROUP BY f.FILM_ID, fl.film_id IN ( " +
                "SELECT film_id " +
                "FROM films_likes " +
                ") " +
                "ORDER BY COUNT(fl.film_id) DESC";
        String sortedByYear = "SELECT F.*, M.*, FD.DIRECTOR_ID " +
                "FROM FILM_DIRECTOR FD " +
                "JOIN FILMS F on F.FILM_ID = FD.FILM_ID " +
                "JOIN MPA M on F.mpa_id = M.MPA_ID " +
                "WHERE DIRECTOR_ID = ? " +
                "GROUP BY f.FILM_ID, F.RELEASE_DATE " +
                "ORDER BY EXTRACT(YEAR FROM cast(F.RELEASE_DATE AS date))";
        List<Film> films;
        if (sortBy.equals("likes")) {
            films = jdbcTemplate.query(sortedByLikes, FilmDbStorage::makeFilm, directorId);
        } else {
            films = jdbcTemplate.query(sortedByYear, FilmDbStorage::makeFilm, directorId);
        }

        return films;
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        String sql = "SELECT f.*, M.* " +
                "FROM FILMS_LIKES " +
                "JOIN FILMS_LIKES fl ON fl.FILM_ID = FILMS_LIKES.FILM_ID " +
                "JOIN FILMS f on f.film_id = fl.film_id " +
                "JOIN MPA M on f.mpa_id = M.MPA_ID " +
                "WHERE fl.USER_ID = ? AND FILMS_LIKES.USER_ID = ?";

        return jdbcTemplate.query(sql, FilmDbStorage::makeFilm, userId, friendId);
    }

    static Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        long duration = rs.getLong("duration");
        Mpa mpa = new Mpa(rs.getInt("mpa.mpa_id"), rs.getString("mpa.name"));

        return new Film(id, name, description, releaseDate, duration, mpa, new LinkedHashSet<>(), new LinkedHashSet<>());
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

    private void addDirectors(Film film) {
        if (film.getDirectors() != null) {
            String updateGenres = "INSERT INTO FILM_DIRECTOR (film_id, DIRECTOR_ID) VALUES (?, ?)";
            jdbcTemplate.batchUpdate(
                    updateGenres, film.getDirectors(), film.getDirectors().size(),
                    (ps, dir) -> {
                        ps.setInt(1, film.getId());
                        ps.setInt(2, dir.getId());
                    });
        } else film.setDirectors(new LinkedHashSet<>());
    }

    private void deleteDirectors(Film film) {
        String deleteDirectors = "DELETE FROM FILM_DIRECTOR WHERE film_id = ?";
        jdbcTemplate.update(deleteDirectors, film.getId());
    }
}