package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
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
    protected final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        validate(film);
        film.setId(filmId++);

        films.put(film.getId(), film);
        log.info("Фильм {} добавлен в коллекцию", film.getName());
        return film;
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) throw new ValidationException("Такого фильма нет");
        films.remove(film.getId());
        validate(film);
        films.put(film.getId(), film);
        log.info("Информация о фильме {} обновлена", film.getName());
        return film;
    }

    void validate(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_FILM_DATE))
            throw new ValidationException("В то время кино еще не было");
        Collection<Film> filmCollection = films.values();
        if (filmCollection.stream().anyMatch(fl -> fl.getName().equals(film.getName())
            && fl.getReleaseDate().equals(film.getReleaseDate())))
                throw new ValidationException("Такой фильм уже есть");
    }
}