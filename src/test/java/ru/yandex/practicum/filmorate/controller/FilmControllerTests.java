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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class FilmControllerTests {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final Film film = new Film(1, "Film", "good film", LocalDate.of(2020, 5, 5), 120, new Mpa(1, "G"), null, null);
    private final Film film2 = new Film(2, "2 Film", "good film 2", LocalDate.of(2019, 5, 5), 111, new Mpa(2, "PG"), null, null);
    private final User user = new User(1, "test@test.com", "login", "name", LocalDate.of(1995, 5, 5));


    @Autowired
    public FilmControllerTests(FilmDbStorage filmDbStorage,
                               UserDbStorage userDbStorage,
                               MockMvc mockMvc,
                               ObjectMapper objectMapper) {
        this.filmDbStorage = filmDbStorage;
        this.userDbStorage = userDbStorage;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    void findAllTest() throws Exception {
        filmDbStorage.create(film);

        mockMvc.perform(
                get("/films")
                )
                .andExpect(status().isOk());
    }

    @Test
    void addFilm() throws Exception {
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Film"))
                .andExpect(jsonPath("$.description").value("good film"))
                .andExpect(jsonPath("$.releaseDate").value("2020-05-05"))
                .andExpect(jsonPath("$.duration").value(120));
    }

    @Test
    void filmUpdateTest() throws Exception {
        filmDbStorage.create(film2);

        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/films/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Film"))
                .andExpect(jsonPath("$.description").value("good film"))
                .andExpect(jsonPath("$.releaseDate").value("2020-05-05"))
                .andExpect(jsonPath("$.duration").value(120));
    }

    @Test
    void filmNotFoundForUpdateTest() throws Exception {
        film.setId(7);
        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof DataException))
                .andExpect(result -> assertEquals("Фильм не найден в базе",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getFilmByIdTest() throws Exception {

        mockMvc.perform(
                        get("/films/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Film"))
                .andExpect(jsonPath("$.description").value("good film"))
                .andExpect(jsonPath("$.releaseDate").value("2020-05-05"))
                .andExpect(jsonPath("$.duration").value(120))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getFilmByIdNotFoundExceptionTest() throws Exception {

        mockMvc.perform(
                        get("/films/18")
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Фильм не найден",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void deleteFilmByIdTest() throws Exception {

        mockMvc.perform(
                        delete("/films/3")
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertTrue(filmDbStorage.getById(3).isEmpty()));
    }

    @Test
    void deleteFilmByIdNotFoundExceptionTest() throws Exception {

        mockMvc.perform(
                        delete("/films/19")
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Фильм не найден",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));

    }

    @Test
    void addLikeAndRemoveTest() throws Exception {
        user.setEmail("test4@test.ru");
        user.setLogin("login4");
        userDbStorage.create(user);
        filmDbStorage.create(film);

        mockMvc.perform(
                        put("/films/1/like/1")
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        delete("/films/1/like/1")
                )
                .andExpect(status().isOk());
    }

    @Test
    void addLikeNotFoundExceptionTest() throws Exception {

        mockMvc.perform(
                        put("/films/29/like/2")
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Пользователь или фильм не найдены",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void removeLikeNotFoundExceptionTest() throws Exception {

        mockMvc.perform(
                        delete("/films/17/like/24")
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Пользователь не найден",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));

    }

    @Test
    void getBestFilmTest() throws Exception {
        film2.setId(2);
        filmDbStorage.update(film2);

        mockMvc.perform(
                        put("/films/2/like/1")
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/films/popular")
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(filmDbStorage.getBestFilms(2, null, null)
                        .get(0).getName(), "2 Film"))
                .andExpect(result -> assertEquals(filmDbStorage.getBestFilms(2, null, null)
                        .get(1).getName(), "Film"));
    }
}
