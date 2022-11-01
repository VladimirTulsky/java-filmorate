package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTests {

    FilmController filmController;
    Film film;

    @BeforeEach
    void filmControllerInit() {
        filmController = new FilmController();
    }

    @Test
    void releaseDateBefore1895Test() {
        film = new Film("Фильм", "Какое-то описание", LocalDate.of(1894, 01, 02), 60);

        assertThrows(ValidationException.class, () -> filmController.validate(film));
    }

    @Test
    void duplicateFilmTest() {
        film = new Film("Фильм", "Какое-то описание", LocalDate.of(1995, 12, 29), 60);
        film.setId(1);
        filmController.films.put(film.getId(), film);
        assertThrows(ValidationException.class, () -> filmController.validate(film));
    }
}
