package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.InternalException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        log.info("Список пользователей отправлен");

        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User getById(int id) {
        if (!userStorage.getUsers().containsKey(id))
            throw new ObjectNotFoundException("Пользователь не найден");
        log.info("Пользователь с id {} отправлен", id);

        return userStorage.getById(id);
    }

    public User deleteById(int id) {
        if (!userStorage.getUsers().containsKey(id))
            throw new ObjectNotFoundException("Пользователь не найден, невозможно удалить.");
        log.info("Пользователь с id {} удален", id);

        return userStorage.deleteById(id);
    }

    public List<User> addFriendship(int firstId, int secondId) {
        if (!userStorage.getUsers().containsKey(firstId) || !userStorage.getUsers().containsKey(secondId))
            throw new ObjectNotFoundException(String.format("Пользователя с id %d или %d не существует", firstId, secondId));
        if (userStorage.getById(firstId).getFriends().contains(secondId))
            throw new InternalException("Пользователи уже являются друзьями");
        userStorage.getById(firstId).getFriends().add(secondId);
        userStorage.getById(secondId).getFriends().add(firstId);
        log.info("Пользователи {} и {} теперь друзья", userStorage.getById(firstId).getName(),
                userStorage.getById(secondId).getName());

        return Arrays.asList(userStorage.getById(firstId), userStorage.getById(secondId));
    }

    public List<User> removeFriendship(int firstId, int secondId) {
        if (!userStorage.getUsers().containsKey(firstId) || !userStorage.getUsers().containsKey(secondId))
            throw new ObjectNotFoundException(String.format("Пользователя с id %d или %d не существует", firstId, secondId));
        if (!userStorage.getById(firstId).getFriends().contains(secondId))
            throw new InternalException("Пользователи не являются друзьями");
        userStorage.getById(firstId).getFriends().remove(secondId);
        userStorage.getById(secondId).getFriends().remove(firstId);
        log.info("Пользователи {} и {} больше не друзья", userStorage.getById(firstId).getName(),
                userStorage.getById(secondId).getName());

        return Arrays.asList(userStorage.getById(firstId), userStorage.getById(secondId));
    }

    public List<User> getFriendsListById(int id) {
        if (!userStorage.getUsers().containsKey(id)) throw new ObjectNotFoundException("Пользователь не найден");
        log.info("Запрос получения списка друзей пользователя {} выполнен", userStorage.getById(id).getName());

        return userStorage.getById(id).getFriends().stream()
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriendsList(int firstId, int secondId) {
        if (!userStorage.getUsers().containsKey(firstId) || !userStorage.getUsers().containsKey(secondId))
            throw new ObjectNotFoundException("Пользователи не найдены");
        User user = userStorage.getById(firstId);
        User otherUser = userStorage.getById(secondId);
        log.info("Список общих друзей {} и {} отправлен", user.getName(), otherUser.getName());

        return user.getFriends().stream()
                .filter(friendId -> otherUser.getFriends().contains(friendId))
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }
}