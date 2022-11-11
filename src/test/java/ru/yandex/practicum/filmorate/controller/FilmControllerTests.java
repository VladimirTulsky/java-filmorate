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
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTests {

    private final ObjectMapper objectMapper;
    private final InMemoryFilmStorage filmStorage;
    private final MockMvc mockMvc;
    private final FilmService filmService;

    @Autowired
    public FilmControllerTests(ObjectMapper objectMapper,
                               InMemoryFilmStorage filmStorage,
                               MockMvc mockMvc,
                               FilmService filmService) {
        this.objectMapper = objectMapper;
        this.filmStorage = filmStorage;
        this.mockMvc = mockMvc;
        this.filmService = filmService;
    }

    @AfterEach
    void resetDB() {
        filmStorage.getFilms().clear();
        filmStorage.setFilmId(1);
    }

    @Test
    void addFilm() throws Exception {
        Film film = new Film("Film", "good film", LocalDate.of(2020, 5, 5), 120);

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Film"))
                .andExpect(jsonPath("$.description").value("good film"))
                .andExpect(jsonPath("$.releaseDate").value("2020-05-05"))
                .andExpect(jsonPath("$.duration").value(120))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void filmReleaseDateFailWithValidationExceptionTest() throws Exception {
        Film film = new Film("Film", "good film", LocalDate.of(1750, 5, 5), 120);

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationException))
                .andExpect(result -> assertEquals("В то время кино еще не было",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void filmDuplicateInternalExceptionTest() throws Exception {
        Film film = new Film("Film", "good film", LocalDate.of(2020, 5, 5), 120);
        filmStorage.create(film);

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InternalException))
                .andExpect(result -> assertEquals("Такой фильм уже есть",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void filmUpdateTest() throws Exception {
        Film film = new Film("Film", "good film", LocalDate.of(2020, 5, 5), 120);
        Film updatedFilm = new Film("Updated film", "bad film", LocalDate.of(2020, 5, 5), 111);
        updatedFilm.setId(1);
        filmStorage.create(film);

        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(updatedFilm))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated film"))
                .andExpect(jsonPath("$.description").value("bad film"))
                .andExpect(jsonPath("$.releaseDate").value("2020-05-05"))
                .andExpect(jsonPath("$.duration").value(111))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void filmNotFoundForUpdateTest() throws Exception {
        Film film = new Film("Film", "good film", LocalDate.of(2020, 5, 5), 120);
        Film updatedFilm = new Film("Updated film", "bad film", LocalDate.of(2020, 5, 5), 111);
        updatedFilm.setId(2);
        filmStorage.create(film);

        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(updatedFilm))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Такого фильма нет",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getFilmByIdTest() throws Exception {
        Film film = new Film("Film", "good film", LocalDate.of(2020, 5, 5), 120);
        filmStorage.create(film);

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
                        get("/films/5")
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Фильм не найден",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void deleteFilmByIdTest() throws Exception {
        Film film = new Film("Film", "good film", LocalDate.of(2020, 5, 5), 120);
        filmStorage.create(film);

        mockMvc.perform(
                        delete("/films/1")
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(0, filmStorage.getFilms().size()));
    }

    @Test
    void deleteFilmByIdNotFoundExceptionTest() throws Exception {

        mockMvc.perform(
                        delete("/films/1")
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Фильм не найден, невозможно удалить",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));

    }

    @Test
    void addLikeAndRemoveTest() throws Exception {
        Film film = new Film("Film", "good film", LocalDate.of(2020, 5, 5), 120);
        filmStorage.create(film);

        mockMvc.perform(
                        put("/films/1/like/1")
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(1, filmStorage.getById(1).getUsersLikes().size()));

        mockMvc.perform(
                        delete("/films/1/like/1")
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(0, filmStorage.getById(1).getUsersLikes().size()));
    }

    @Test
    void addLikeNotFoundExceptionTest() throws Exception {

        mockMvc.perform(
                        put("/films/1/like/1")
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Фильм не найден",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void removeLikeNotFoundExceptionTest() throws Exception {

        mockMvc.perform(
                        delete("/films/1/like/1")
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Фильм не найден",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));

        Film film = new Film("Film", "good film", LocalDate.of(2020, 5, 5), 120);
        filmStorage.create(film);

        mockMvc.perform(
                        delete("/films/1/like/2")
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Лайк от пользователя отсутствует",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getBestFilmTest() throws Exception {
        Film film = new Film("Film", "good film", LocalDate.of(2020, 5, 5), 120);
        Film film2 = new Film("Film2", "good film2", LocalDate.of(2020, 5, 5), 130);
        film.setUsersLikes(Set.of(1, 2, 3));
        film2.setUsersLikes(Set.of(2, 3, 4, 5));
        filmStorage.create(film);
        filmStorage.create(film2);

        mockMvc.perform(
                        get("/films/popular")
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/films/popular?count=2")
                )
                .andExpect(status().isOk());
        assertEquals(filmService.getBestFilms(2).get(0), film2);
        assertEquals(filmService.getBestFilms(2).get(1), film);
    }

}
