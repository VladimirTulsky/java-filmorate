package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    protected final Map<Integer, Film> films = new HashMap<>();

    private int filmId = 1;

    FilmService filmService;

    @Autowired
    public InMemoryFilmStorage(FilmService filmService) {
        this.filmService = filmService;
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        filmService.validate(film);
        checkFilms(film);
        film.setId(filmId++);

        films.put(film.getId(), film);
        log.info("Фильм {} добавлен в коллекцию", film.getName());
        return film;
    }

    @Override
    public Film put(Film film) {
        if (!films.containsKey(film.getId())) throw new ValidationException("Такого фильма нет");
        filmService.validate(film);

        films.put(film.getId(), film);
        log.info("Информация о фильме {} обновлена", film.getName());
        return film;
    }

    @Override
    public Film getById(int id) {
        return films.get(id);
    }

    @Override
    public Film deleteById(int id) {
        Film film = films.get(id);
        films.remove(id);
        return film;
    }


    private void checkFilms(Film film) {
        if (findAll().stream().anyMatch(fl -> fl.getName().equals(film.getName())
                && fl.getReleaseDate().equals(film.getReleaseDate())))
            throw new ValidationException("Такой фильм уже есть");
    }
}
