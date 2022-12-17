package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Mpa> findAll() {
        String sql = "SELECT * FROM mpa";

        return jdbcTemplate.query(sql, MpaDbStorage::makeMpa);
    }

    @Override
    public Optional<Mpa> getById(int id) {
        String sql = "SELECT * FROM mpa WHERE MPA_ID = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, MpaDbStorage::makeMpa, id));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    static Mpa makeMpa(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("mpa_id");
        String name = rs.getString("name");

        return new Mpa(id, name);
    }
}
