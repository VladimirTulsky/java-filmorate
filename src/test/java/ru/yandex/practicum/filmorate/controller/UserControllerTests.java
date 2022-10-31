package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTests {

    UserController userController;
    User user;

    @BeforeEach
    void UserControllerInit() {
        userController = new UserController();
    }

    @Test
    void userWithoutNameTest() {
        user = new User("test@test.ru", "login", LocalDate.of(1990, 05, 06));
        userController.validate(user);
        assertEquals("login", user.getName());

        user = new User("test@test.ru", "login", LocalDate.of(1990, 05, 06));
        user.setName(" ");
        userController.validate(user);
        assertEquals("login", user.getName());
    }

    @Test
    void duplicateUserTest() {
        user = new User("test@test.ru", "login", LocalDate.of(1990, 05, 06));
        user.setId(1);
        userController.users.put(user.getId(), user);
        assertThrows(ValidationException.class, () -> userController.validate(user));
    }
}
