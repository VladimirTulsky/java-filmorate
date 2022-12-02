package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UtilityService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UtilityService utilityService;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate, UtilityService utilityService){
        this.jdbcTemplate=jdbcTemplate;
        this.utilityService = utilityService;
    }

    @Override
    public Collection<User> findAll() {
        String sql = "select * from USERS";

        return jdbcTemplate.query(sql, utilityService::makeUser);
    }

    @Override
    public User create(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) " +
                "VALUES ( ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        return user;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sql,
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());

        return user;
    }

    @Override
    public Optional<User> getById(int id) {
        String sql = "select * from USERS where ID = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, id);
        if (!filmRows.next()) {
            return Optional.empty();
        }

        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, utilityService::makeUser, id));
    }

    @Override
    public Optional<User> deleteById(int id) {
        String sql = "delete from USERS where ID = ?";
        Optional<User> user = getById(id);
        jdbcTemplate.update(sql, id);
        log.info("Пользователь с id {} удален", id);

        return user;
    }

    @Override
    public List<Integer> followUser(int followingId, int followerId) {
        String sqlForWrite = "INSERT INTO FRIENDSHIP (USER_ID, FRIEND_ID) " +
                "VALUES (?, ?)";
        jdbcTemplate.update(sqlForWrite, followingId, followerId);

        return List.of(followingId, followerId);
    }

    @Override
    public List<Integer> unfollowUser(int followingId, int followerId) {
        String sql = "DELETE FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sql, followingId, followerId);

        return List.of(followingId, followerId);
    }

    @Override
    public List<User> getFriendsListById(int id) {
        String sql = "SELECT id, email, login, name, birthday " +
                "FROM USERS " +
                "LEFT JOIN friendship f on users.id = f.friend_id " +
                "where user_id = ?";

        return jdbcTemplate.query(sql, utilityService::makeUser, id);
    }

    @Override
    public List<User> getCommonFriendsList(int firstId, int secondId) {
        String sql = "SELECT id, email, login, name, birthday " +
                "FROM friendship AS f " +
                "LEFT JOIN users u ON u.id = f.friend_id " +
                "WHERE f.user_id = ? AND f.friend_id IN ( " +
                "SELECT friend_id " +
                "FROM friendship AS f " +
                "LEFT JOIN users AS u ON u.id = f.friend_id " +
                "WHERE f.user_id = ? )";

        return jdbcTemplate.query(sql, utilityService::makeUser, firstId, secondId);
    }
}
