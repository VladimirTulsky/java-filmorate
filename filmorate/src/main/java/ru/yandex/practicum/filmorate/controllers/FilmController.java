package ru.yandex.practicum.filmorate.controllers;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    HashMap<Integer, Film> films = new HashMap<>();

    @GetMapping("/films")
    public HashMap<Integer, Film> getFilms() {
        log.info("Получен /GET запрос о выводе фильмов");
        return films;
    }

    @PostMapping(value = "/film/add")
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Получен /POST запрос добавление фильма");
        try {
            log.info("Проверка даты фильма");
            releaseDateShouldBeAfter28Dec1895Year(film);
            log.info("Проверка наличия в списке");
            if (films.containsKey(film.getId())) {
                throw new ValidationException("Такой фильм уже есть в списке");
            } else {
                films.put(film.getId(), film);
                log.info("Фильм с названием " + film.getName() + " добавлен");
            }
        } catch (Exception | ValidationException e) {
            throw new RuntimeException(e);
        }
        return film;
    }

    @PutMapping(value = "/film/update")
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Получен /PUT запрос обновление фильма");
        try {
            log.info("Проверка даты фильма");
            releaseDateShouldBeAfter28Dec1895Year(film);
            log.info("Проверка наличия в списке");
            if (!films.containsKey(film.getId())) {
                throw new ValidationException("Такого фильма нету в списке");
            } else {
                films.put(film.getId(), film);
                log.info("Фильм с названием " + film.getName() + " обновлен");
            }
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
        return film;
    }

    @SneakyThrows
    private Film releaseDateShouldBeAfter28Dec1895Year(Film film) {
        LocalDate filmsBirthDate = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(filmsBirthDate)) {
            log.info("Дата релиза фильма не может быть раньше 1895 года 28 декабря");
            throw new ValidationException("Дата релиза фильма не может быть раньше 1895 года 28 декабря");
        }
        return film;
    }
}
