package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.InternalException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmStorageTest {
    InMemoryFilmStorage filmStorage;
    Film film;

    @BeforeEach
    void init() {
        filmStorage = new InMemoryFilmStorage();
    }

    @Test
    void releaseDateBefore1895Test() {
        film = new Film("Фильм", "Какое-то описание", LocalDate.of(1894, 1, 2), 60);

        assertThrows(ValidationException.class, () -> filmStorage.validate(film));
    }

    @Test
    void duplicateFilmTest() {
        film = new Film("Фильм", "Какое-то описание", LocalDate.of(1995, 12, 29), 60);
        film.setId(1);
        filmStorage.getFilms().put(film.getId(), film);
        assertThrows(InternalException.class, () -> filmStorage.checkFilms(film));
    }
}
