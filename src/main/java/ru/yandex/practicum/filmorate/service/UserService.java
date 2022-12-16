package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@Slf4j
public class UserService {
    private final UserStorage userDbStorage;

    @Autowired
    public UserService(UserStorage userDbStorage) {
        this.userDbStorage = userDbStorage;
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

    public List<Integer> followUser(int followerId, int followingId) {
        usersValidation(followerId, followingId);
        log.info("Пользователь {} подписался на {}", followerId, followingId);

        return userDbStorage.followUser(followerId, followingId);
    }

    public List<Integer> unfollowUser(int followerId, int followingId) {
        log.info("Пользователь {} отписался от {}", followerId, followingId);

        return userDbStorage.unfollowUser(followerId, followingId);
    }

    public List<User> getFriendsListById(int id) {
        getById(id);
        log.info("Запрос получения списка друзей пользователя {} выполнен", id);

        return userDbStorage.getFriendsListById(id);
    }

    public List<User> getCommonFriendsList(int firstId, int secondId) {
        usersValidation(firstId, secondId);
        log.info("Список общих друзей {} и {} отправлен", firstId, secondId);

        return userDbStorage.getCommonFriendsList(firstId, secondId);
    }

    public void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
    }

    public void usersValidation(int followerId, int followingId) {
        getById(followerId);
        getById(followingId);
    }
}