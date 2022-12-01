package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Service
@Slf4j
public class GenreService {

    JdbcTemplate jdbcTemplate;

    public GenreService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Genre> findAll() {
        String sql = "SELECT * FROM genre";

        return jdbcTemplate.query(sql, this::makeGenre);
    }

    public Genre getById(int id) {
        String sql = "SELECT * FROM genre WHERE genre_id = ?";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql, id);
        if (!genreRows.next()) {
            log.warn("Жанр {} не найден.", id);
            throw new ObjectNotFoundException("Жанр не найден");
        }

        return jdbcTemplate.queryForObject(sql, this::makeGenre, id);
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("genre_id");
        String name = rs.getString("name");

        return new Genre(id, name);
    }
}
