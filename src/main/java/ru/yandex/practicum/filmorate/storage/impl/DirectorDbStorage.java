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
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    @Override
    public List<Director> findAll() {
        String sql = "SELECT * FROM directors";

        return jdbcTemplate.query(sql, DirectorDbStorage::makeDirector);
    }

    @Override
    public Director create(Director director) {
        String sql = "INSERT INTO directors (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"director_id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        director.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        return director;
    }

    @Override
    public Director update(Director director) {
        String sql = "UPDATE directors SET name = ? WHERE director_id = ?";
        int result = jdbcTemplate.update(sql, director.getName(), director.getId());
        if (result < 1) throw new DataException("Режиссер не найден в базе");

        return director;
    }

    @Override
    public Optional<Director> getById(long id) {
        String sql = "SELECT * FROM directors WHERE director_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, DirectorDbStorage::makeDirector, id));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public int deleteById(long id) {
        String sql = "DELETE FROM directors WHERE director_id = ?";
        return jdbcTemplate.update(sql, id);
    }

    static Director makeDirector(ResultSet rs, int rowNum) throws SQLException {
        long director_id = rs.getLong("director_id");
        String name = rs.getString("name");

        return new Director(director_id, name);
    }

    @Override
    public void loadDirectors(List<Film> films) {
        String sqlDirectors = "SELECT film_id, D.* " +
                "FROM FILM_DIRECTOR " +
                "JOIN DIRECTORS D on D.DIRECTOR_ID = FILM_DIRECTOR.DIRECTOR_ID " +
                "WHERE FILM_ID IN (:ids)";
        List<Long> ids = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());
        Map<Long, Film> filmMap = films.stream()
                .collect(Collectors.toMap(Film::getId, film -> film, (a, b) -> b));
        SqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        SqlRowSet sqlRowSet = namedJdbcTemplate.queryForRowSet(sqlDirectors, parameters);
        while (sqlRowSet.next()) {
            long filmId = sqlRowSet.getLong("film_id");
            long directorId = sqlRowSet.getLong("director_id");
            String name = sqlRowSet.getString("name");
            filmMap.get(filmId).getDirectors().add(new Director(directorId, name));
        }
        films.forEach(film -> film.getDirectors().addAll(filmMap.get(film.getId()).getDirectors()));
    }
}
