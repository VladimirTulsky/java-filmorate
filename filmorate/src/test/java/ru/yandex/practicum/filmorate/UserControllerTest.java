package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@SpringBootTest
public class UserControllerTest {

    static LocalDate time;
    static User user;
    static User user2;
    static UserController userController = new UserController();

    @BeforeEach
    void init() {
        time = LocalDate.of(2002, 8, 02);
        user = new User(1, "test@", "test", "test", time);
        user2 = new User(1, "test2@", "test2", "test2", time);
    }

    @AfterEach
    void cleaner() {
        userController.getUsers().clear();
    }

    @Test
    void shouldAddUserToMap() {
        userController.createUser(user);
        Assertions.assertEquals(1, userController.getUsers().size());
    }

    @Test
    void shouldUpdateUserInMap() {
        userController.createUser(user);
        userController.updateUser(user2);
        Assertions.assertEquals("test2@", user2.getEmail());
        Assertions.assertEquals("test2", user2.getLogin());
        Assertions.assertEquals("test2", user2.getName());
    }
}
