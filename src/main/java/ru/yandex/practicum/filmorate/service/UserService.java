package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InternalException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.util.*;

@Service
@Slf4j
public class UserService {
    private final UserDbStorage userDbStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(UserDbStorage userDbStorage, JdbcTemplate jdbcTemplate) {
        this.userDbStorage = userDbStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<User> findAll() {
        log.info("Список пользователей отправлен");

        return userDbStorage.findAll();
    }

    public User create(User user) {
        validate(user);
        log.info("Пользователь добавлен");

        return userDbStorage.create(user);
    }

    public User update(User user) {
        validate(user);
        if (userDbStorage.getById(user.getId()).isEmpty()) {
            log.warn("Пользователь с id {} не найден", user.getId());
            throw new ObjectNotFoundException("Пользователь не найден");
        }
        log.info("Пользователь {} обновлен", user.getId());

        return userDbStorage.update(user);
    }

    public User getById(int id) {
        log.info("Пользователь с id {} отправлен", id);

        return userDbStorage.getById(id).orElseThrow(() -> {
            log.warn("Пользователь с идентификатором {} не найден.", id);
            throw new ObjectNotFoundException("Пользователь не найден");
        });
    }

    public User deleteById(int id) {
        log.info("Пользователь {} удален", id);

        return userDbStorage.deleteById(id).orElseThrow(() -> {
            log.warn("Пользователь не найден");
            throw new ObjectNotFoundException("Пользователь не найден");
        });
    }

    public List<Integer> followUser(int followingId, int followerId) {
        if (userDbStorage.getById(followingId).isEmpty() || userDbStorage.getById(followerId).isEmpty()) {
            log.warn("Пользователи с id {} и(или) {} не найден(ы)", followingId, followerId);
            throw new ObjectNotFoundException("Пользователи не найдены");
        }
        String checkFriendship = "SELECT * FROM FRIENDSHIP WHERE user_id = ? AND friend_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(checkFriendship, followingId, followerId);
        if (userRows.first()) {
            log.warn("Пользователь уже подписан");
            throw new InternalException("Пользователь уже подписан");
        }
        log.info("Пользователь {} подписался на {}", followingId, followerId);

        return userDbStorage.followUser(followingId, followerId);
    }

    public List<Integer> unfollowUser(int followingId, int followerId) {
        String checkFriendship = "SELECT * FROM FRIENDSHIP WHERE user_id = ? AND friend_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(checkFriendship, followingId, followerId);
        if (!userRows.first()) {
            log.warn("Пользователь не подписан");
            throw new InternalException("Пользователь не подписан");
        }
        log.info("Пользователь {} отписался от {}", followerId, followingId);

        return userDbStorage.unfollowUser(followingId, followerId);
    }

    public List<User> getFriendsListById(int id) {
        if (userDbStorage.getById(id).isEmpty()) {
            log.warn("Пользователь с id {} не найден", id);
            throw new ObjectNotFoundException("Пользователь не найден");
        }
        log.info("Запрос получения списка друзей пользователя {} выполнен", id);

        return userDbStorage.getFriendsListById(id);
    }

    public List<User> getCommonFriendsList(int firstId, int secondId) {
        if (userDbStorage.getById(firstId).isEmpty() || userDbStorage.getById(secondId).isEmpty()) {
            log.warn("Пользователи с id {} и {} не найдены", firstId, secondId);
            throw new ObjectNotFoundException("Пользователи не найдены");
        }
        log.info("Список общих друзей {} и {} отправлен", firstId, secondId);

        return userDbStorage.getCommonFriendsList(firstId, secondId);
    }

    public void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
    }
}