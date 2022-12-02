package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.UtilityService;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UtilityService utilityService;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate, UtilityService utilityService) {
        this.jdbcTemplate = jdbcTemplate;
        this.utilityService = utilityService;
    }

    @Override
    public Collection<Genre> findAll() {
        String sql = "SELECT * FROM genre";

        return jdbcTemplate.query(sql, utilityService::makeGenre);
    }

    @Override
    public Optional<Genre> getById(int id) {
        String sql = "SELECT * FROM genre WHERE genre_id = ?";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql, id);
        if (!genreRows.next()) {
            return Optional.empty();
        }

        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, utilityService::makeGenre, id));
    }
}
