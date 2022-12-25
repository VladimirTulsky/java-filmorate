package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DataException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> findAll() {
        String sql = "select * from USERS";

        return jdbcTemplate.query(sql, UserDbStorage::makeUser);
    }

    @Override
    public User create(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) " +
                "VALUES ( ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"user_id"});
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
                "WHERE USER_ID = ?";
        int result = jdbcTemplate.update(sql,
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        if (result < 1) throw new DataException("Пользователь не найден в базе");
        return user;
    }

    @Override
    public Optional<User> getById(int id) {
        String sql = "select * from USERS where USER_ID = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, UserDbStorage::makeUser, id));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> deleteById(int id) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        Optional<User> user = getById(id);
        jdbcTemplate.update(sql, id);

        return user;
    }

    @Override
    public List<Integer> followUser(int followerId, int followingId) {
        String sqlForWrite = "MERGE INTO FRIENDSHIP (USER_ID, FRIEND_ID) " +
                "VALUES (?, ?)";
        jdbcTemplate.update(sqlForWrite, followerId, followingId);

        return List.of(followerId, followingId);
    }

    @Override
    public List<Integer> unfollowUser(int followerId, int followingId) {
        String sql = "DELETE FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sql, followerId, followingId);

        return List.of(followerId, followingId);
    }

    @Override
    public List<User> getFriendsListById(int id) {
        String sql = "SELECT USERS.USER_ID, email, login, name, birthday " +
                "FROM USERS " +
                "LEFT JOIN friendship f on users.USER_ID = f.friend_id " +
                "where f.user_id = ?";

        return jdbcTemplate.query(sql, UserDbStorage::makeUser, id);
    }

    @Override
    public List<User> getCommonFriendsList(int firstId, int secondId) {
        String sql = "SELECT u.USER_ID, email, login, name, birthday " +
                "FROM friendship AS f " +
                "LEFT JOIN users u ON u.USER_ID = f.friend_id " +
                "WHERE f.user_id = ? AND f.friend_id IN ( " +
                "SELECT friend_id " +
                "FROM friendship AS f " +
                "LEFT JOIN users AS u ON u.USER_ID = f.friend_id " +
                "WHERE f.user_id = ? )";

        return jdbcTemplate.query(sql, UserDbStorage::makeUser, firstId, secondId);
    }

    static User makeUser(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();

        return new User(id, email, login, name, birthday);
    }
}
