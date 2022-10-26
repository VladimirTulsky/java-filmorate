package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);
    private int filmId = 1;
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        validate(film);
        checkFilm(film);
        film.setId(filmId++);

        films.put(film.getId(), film);
        log.info("Фильм {} добавлен в коллекцию", film.getName());
        return film;
    }

    @PutMapping
    public Film put(@RequestBody Film film) {
        validate(film);
        if (!films.containsKey(film.getId())) throw new ValidationException("Такого фильма нет");
        films.remove(film.getId());
        checkFilm(film);
        films.put(film.getId(), film);
        log.info("Информация о фильме {} обновлена", film.getName());
        return film;
    }

    private void validate(@RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank())
            throw new ValidationException("Добавьте название фильма");
        if (film.getReleaseDate().isBefore(FIRST_FILM_DATE) || film.getDuration() < 0)
            throw new ValidationException("В то время кино еще не было или продолжительность неверная");
        if (film.getDescription().length() > 200)
            throw new ValidationException("Слишком длинное описание");
    }

    private void checkFilm(@RequestBody Film film) {
        Collection<Film> filmCollection = films.values();
        for (Film fl : filmCollection) {
            if (film.getName().equals(fl.getName()) && film.getReleaseDate().equals(fl.getReleaseDate()))
                throw new ValidationException("Такой фильм уже есть");
        }
    }
}