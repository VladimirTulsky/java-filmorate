package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.DataException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class UserControllerTests {

    private final ObjectMapper objectMapper;
    private final UserDbStorage userDbStorage;
    private final MockMvc mockMvc;
    private final User user = new User(1, "test@test.com", "login", "name", LocalDate.of(1995, 5, 5));
    private final User user2 = new User(2, "new@test.ru", "new_login", "name", LocalDate.of(1990, 5, 6));

    @Autowired
    public UserControllerTests(ObjectMapper objectMapper,
                               UserDbStorage userDbStorage,
                               MockMvc mockMvc) {
        this.objectMapper = objectMapper;
        this.userDbStorage = userDbStorage;
        this.mockMvc = mockMvc;
    }

    @Test
    void findAllTest() throws Exception {
        user.setEmail("test1@test.ru");
        user.setLogin("login1");
        userDbStorage.create(user);

        mockMvc.perform(
                        get("/users")
                )
                .andExpect(status().isOk());
    }

    @Test
    void addUser() throws Exception {

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.login").value("login"))
                .andExpect(jsonPath("$.birthday").value("1995-05-05"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void userUpdateTest() throws Exception {

        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(user2))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@test.ru"))
                .andExpect(jsonPath("$.login").value("new_login"));

        mockMvc.perform(
                        get("/users/2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@test.ru"))
                .andExpect(jsonPath("$.login").value("new_login"));

    }

    @Test
    void userNotFoundForUpdateTest() throws Exception {
        user2.setId(21);

        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(user2))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof DataException))
                .andExpect(result -> assertEquals("Пользователь не найден в базе",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getUserByIdTest() throws Exception {

        mockMvc.perform(
                        get("/users/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.birthday").value("1995-05-05"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getUserByIdNotFoundExceptionTest() throws Exception {

        mockMvc.perform(
                        get("/users/21")
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Пользователь не найден",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void deleteUserByIdTest() throws Exception {
        user.setEmail("test2@test.ru");
        user.setLogin("login2");
        userDbStorage.create(user);
        user.setEmail("test3@test.ru");
        user.setLogin("login3");
        userDbStorage.create(user);

        mockMvc.perform(
                        delete("/users/3")
                )
                .andExpect(status().isOk());
    }

    @Test
    void deleteUserByIdNotFoundExceptionTest() throws Exception {

        mockMvc.perform(
                        delete("/users/34")
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Пользователь не найден",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void addFriendsTest() throws Exception {

        mockMvc.perform(
                        put("/users/1/friends/2")
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(2, userDbStorage.getFriendsListById(1).get(0).getId()));

    }

    @Test
    void addFriendsNotFoundTest() throws Exception {

        mockMvc.perform(
                        put("/users/9/friends/23")
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Пользователь не найден",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void removeFriendsTest() throws Exception {
        userDbStorage.create(user2);
        userDbStorage.followUser(2, 1);

        mockMvc.perform(
                        delete("/users/2/friends/1")
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(0, userDbStorage.getFriendsListById(2).size()));
    }

    @Test
    void getFriendsListByIdTest() throws Exception {
        userDbStorage.followUser(1,2);

        mockMvc.perform(
                        get("/users/1/friends")
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(1, userDbStorage.getFriendsListById(1).size()));
        userDbStorage.unfollowUser(1,2);
    }

    @Test
    void getFriendsListByIdNotFoundTest() throws Exception {

        mockMvc.perform(
                        get("/users/34/friends")
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Пользователь не найден",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getCommonFriendsListTest() throws Exception {
        userDbStorage.followUser(4, 2);
        userDbStorage.followUser(1, 2);

        mockMvc.perform(
                        get("/users/1/friends/common/4")
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(2, userDbStorage.getFriendsListById(1).get(0).getId()))
                .andExpect(result -> assertEquals(2, userDbStorage.getFriendsListById(4).get(0).getId()));
        userDbStorage.unfollowUser(4, 2);
        userDbStorage.unfollowUser(1, 2);
    }

    @Test
    void getCommonFriendsNotFoundTest() throws Exception {
        mockMvc.perform(
                        get("/users/9/friends/common/23")
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Пользователь не найден",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }
}
