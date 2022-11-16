package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.InternalException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {

    private final ObjectMapper objectMapper;
    private final InMemoryUserStorage userStorage;
    private final MockMvc mockMvc;
    private final UserService userService;

    @Autowired
    public UserControllerTests(ObjectMapper objectMapper,
                               InMemoryUserStorage userStorage,
                               MockMvc mockMvc,
                               UserService userService) {
        this.objectMapper = objectMapper;
        this.userStorage = userStorage;
        this.mockMvc = mockMvc;
        this.userService = userService;
    }

    @AfterEach
    void resetDB() {
        userStorage.getUsers().clear();
        userStorage.setUserId(1);
    }

    @Test
    void findAllTest() throws Exception {

        mockMvc.perform(
                        get("/users")
                )
                .andExpect(status().isOk());
    }

    @Test
    void addUser() throws Exception {
        User user = new User("vladimir@test.ru", "vladimir21", LocalDate.of(1990, 5, 6));
        user.setName("Vladimir");

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Vladimir"))
                .andExpect(jsonPath("$.login").value("vladimir21"))
                .andExpect(jsonPath("$.birthday").value("1990-05-06"))
                .andExpect(jsonPath("$.email").value("vladimir@test.ru"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void userWithEmptyNameTest() throws Exception {
        User user = new User("test@test.ru", "login", LocalDate.of(1990, 5, 6));

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("login"));
    }

    @Test
    void userDuplicateInternalExceptionTest() throws Exception {
        User user = new User("test@test.ru", "login", LocalDate.of(1990, 5, 6));
        userStorage.create(user);

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InternalException))
                .andExpect(result -> assertEquals("Пользователь с таким email или login уже существует",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void userUpdateTest() throws Exception {
        User user = new User("test@test.ru", "login", LocalDate.of(1990, 5, 6));
        User updatedUser = new User("new@test.ru", "new_login", LocalDate.of(1990, 5, 6));
        userStorage.create(user);
        updatedUser.setId(1);

        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(updatedUser))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@test.ru"))
                .andExpect(jsonPath("$.login").value("new_login"));
    }

    @Test
    void userNotFoundForUpdateTest() throws Exception {
        User user = new User("test@test.ru", "login", LocalDate.of(1990, 5, 6));
        User updatedUser = new User("new@test.ru", "new_login", LocalDate.of(1990, 5, 6));
        userStorage.create(user);
        updatedUser.setId(2);

        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(updatedUser))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Пользователя не существует, необходима регистрация нового пользователя",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getUserByIdTest() throws Exception {
        User user = new User("test@test.ru", "login", LocalDate.of(1990, 5, 6));
        userStorage.create(user);

        mockMvc.perform(
                        get("/users/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("login"))
                .andExpect(jsonPath("$.login").value("login"))
                .andExpect(jsonPath("$.birthday").value("1990-05-06"))
                .andExpect(jsonPath("$.email").value("test@test.ru"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getUserByIdNotFoundExceptionTest() throws Exception {
        mockMvc.perform(
                        get("/users/1")
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Пользователь не найден",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void deleteUserByIdTest() throws Exception {
        User user = new User("test@test.ru", "login", LocalDate.of(1990, 5, 6));
        userStorage.create(user);

        mockMvc.perform(
                        delete("/users/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("login"))
                .andExpect(jsonPath("$.login").value("login"))
                .andExpect(jsonPath("$.birthday").value("1990-05-06"))
                .andExpect(jsonPath("$.email").value("test@test.ru"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteUserByIdNotFoundExceptionTest() throws Exception {
        mockMvc.perform(
                        delete("/users/1")
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Пользователь не найден, невозможно удалить",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void addFriendsTest() throws Exception {
        User user = new User("test@test.ru", "login", LocalDate.of(1990, 5, 6));
        User user2 = new User("test2@test.ru", "login2", LocalDate.of(1991, 5, 6));
        userStorage.create(user);
        userStorage.create(user2);

        mockMvc.perform(
                        put("/users/1/friends/2")
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertTrue(userStorage.getById(1).getFriends().contains(2)))
                .andExpect(result -> assertTrue(userStorage.getById(2).getFriends().contains(1)));
    }

    @Test
    void addFriendsNotFoundTest() throws Exception {

        mockMvc.perform(
                        put("/users/1/friends/2")
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Пользователя с id 1 или 2 не существует",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void addFriendsAlreadySetFriendshipTest() throws Exception {
        User user = new User("test@test.ru", "login", LocalDate.of(1990, 5, 6));
        User user2 = new User("test2@test.ru", "login2", LocalDate.of(1991, 5, 6));
        userStorage.create(user);
        userStorage.create(user2);
        userService.addFriendship(1, 2);

        mockMvc.perform(
                        put("/users/1/friends/2")
                )
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InternalException))
                .andExpect(result -> assertEquals("Пользователи уже являются друзьями",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void removeFriendsTest() throws Exception {
        User user = new User("test@test.ru", "login", LocalDate.of(1990, 5, 6));
        User user2 = new User("test2@test.ru", "login2", LocalDate.of(1991, 5, 6));
        userStorage.create(user);
        userStorage.create(user2);
        userService.addFriendship(1, 2);

        mockMvc.perform(
                        delete("/users/1/friends/2")
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertTrue(userStorage.getById(1).getFriends().isEmpty()))
                .andExpect(result -> assertTrue(userStorage.getById(2).getFriends().isEmpty()));
    }

    @Test
    void removeFriendsNotFoundTest() throws Exception {

        mockMvc.perform(
                        delete("/users/1/friends/2")
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Пользователя с id 1 или 2 не существует",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void removeFriendsWithoutFriendshipTest() throws Exception {
        User user = new User("test@test.ru", "login", LocalDate.of(1990, 5, 6));
        User user2 = new User("test2@test.ru", "login2", LocalDate.of(1991, 5, 6));
        userStorage.create(user);
        userStorage.create(user2);

        mockMvc.perform(
                        delete("/users/1/friends/2")
                )
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InternalException))
                .andExpect(result -> assertEquals("Пользователи не являются друзьями",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getFriendsListByIdTest() throws Exception {
        User user = new User("test@test.ru", "login", LocalDate.of(1990, 5, 6));
        User user2 = new User("test2@test.ru", "login2", LocalDate.of(1991, 5, 6));
        userStorage.create(user);
        userStorage.create(user2);
        userService.addFriendship(1, 2);

        mockMvc.perform(
                        get("/users/1/friends")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(user2))))
                .andExpect(result -> assertTrue(userService.getFriendsListById(1).contains(user2)))
                .andExpect(result -> assertTrue(userService.getFriendsListById(2).contains(user)));
    }

    @Test
    void getFriendsListByIdNotFoundTest() throws Exception {
        mockMvc.perform(
                        get("/users/1/friends")
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Пользователь не найден",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getCommonFriendsListTest() throws Exception {
        User user = new User("test@test.ru", "login", LocalDate.of(1990, 5, 6));
        User user2 = new User("test2@test.ru", "login2", LocalDate.of(1991, 5, 6));
        User user3 = new User("test3@test.ru", "login3", LocalDate.of(1991, 5, 6));
        userStorage.create(user);
        userStorage.create(user2);
        userStorage.create(user3);
        userService.addFriendship(1, 2);
        userService.addFriendship(1, 3);

        mockMvc.perform(
                        get("/users/2/friends/common/3")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(user))))
                .andExpect(result -> assertTrue(userService.getById(2).getFriends().contains(1)))
                .andExpect(result -> assertTrue(userService.getById(3).getFriends().contains(1)));
    }

    @Test
    void getCommonFriendsNotFoundTest() throws Exception {
        mockMvc.perform(
                        get("/users/1/friends/common/2")
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Пользователи не найдены",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }
}
