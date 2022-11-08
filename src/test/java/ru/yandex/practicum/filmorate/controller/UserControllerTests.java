package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTests {
    UserController userController;
    User user;

//    @BeforeEach
//    void UserControllerInit() {
//        userController = new UserController();
//    }
//
//    @Test
//    void userWithoutNameTest() {
//        user = new User("test@test.ru", "login", LocalDate.of(1990, 5, 6));
//        userController.validate(user);
//        assertEquals("login", user.getName());
//
//        user = new User("test@test.ru", "login", LocalDate.of(1990, 5, 6));
//        user.setName(" ");
//        userController.validate(user);
//        assertEquals("login", user.getName());
//    }
//
//    @Test
//    void duplicateUserTest() {
//        user = new User("test@test.ru", "login", LocalDate.of(1990, 5, 6));
//        user.setId(1);
//        userController.users.put(user.getId(), user);
//        assertThrows(ValidationException.class, () -> userController.validate(user));
//    }
}
