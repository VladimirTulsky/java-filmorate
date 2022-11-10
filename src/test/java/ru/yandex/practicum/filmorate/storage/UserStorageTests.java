package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.InternalException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserStorageTests {

    InMemoryUserStorage userStorage;
    User user;

    @BeforeEach
    void init() {
        userStorage = new InMemoryUserStorage();
    }

    @Test
    void userWithoutNameTest() {
        user = new User("test@test.ru", "login", LocalDate.of(1990, 5, 6));
        userStorage.validate(user);
        assertEquals("login", user.getName());

        user = new User("test@test.ru", "login", LocalDate.of(1990, 5, 6));
        user.setName(" ");
        userStorage.validate(user);
        assertEquals("login", user.getName());
    }

    @Test
    void duplicateUserTest() {
        user = new User("test@test.ru", "login", LocalDate.of(1990, 5, 6));
        user.setId(1);
        userStorage.getUsers().put(user.getId(), user);
        assertThrows(InternalException.class, () -> userStorage.checkUsers(user));
    }
}
