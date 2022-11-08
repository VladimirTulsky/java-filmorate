package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    Collection<User> findAll();
    User create(User user);
    User put(User user);
    User getById(int id);
    User deleteById(int id);
}
