package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;

public interface UserStorage {

    Map<Integer, User> getUsers();
    Collection<User> findAll();
    User create(User user);
    User update(User user);
    User getById(int id);
    User deleteById(int id);
}
