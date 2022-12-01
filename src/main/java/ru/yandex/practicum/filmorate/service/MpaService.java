package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Service
@Slf4j
public class MpaService {

    JdbcTemplate jdbcTemplate;

    public MpaService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Mpa> findAll() {
        String sql = "SELECT * FROM mpa";

        return jdbcTemplate.query(sql, this::makeMpa);
    }

    public Mpa getById(int id) {
        String sql = "SELECT * FROM mpa WHERE id = ?";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sql, id);
        if (!mpaRows.next()) {
            log.warn("Рейтинг {} не найден.", id);
            throw new ObjectNotFoundException("Рейтинг не найден");
        }

        return jdbcTemplate.queryForObject(sql, this::makeMpa, id);
    }

    private Mpa makeMpa(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");

        return new Mpa(id, name);
    }

}
