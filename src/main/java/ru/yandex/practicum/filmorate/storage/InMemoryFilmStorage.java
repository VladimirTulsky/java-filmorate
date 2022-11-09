package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InternalException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);
    private int filmId = 1;

    @Override
    public Map<Integer, Film> getFilms() {
        return films;
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        validate(film);
        checkFilms(film);
        film.setId(filmId++);
        films.put(film.getId(), film);
        log.info("Фильм {} добавлен в коллекцию", film.getName());

        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) throw new ObjectNotFoundException("Такого фильма нет");
        validate(film);
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

    public void validate(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_FILM_DATE))
            throw new ValidationException("В то время кино еще не было");
    }

    public void checkFilms(Film film) {
        if (findAll().stream().anyMatch(fl -> fl.getName().equals(film.getName())
                && fl.getReleaseDate().equals(film.getReleaseDate())))
            throw new InternalException("Такой фильм уже есть");
    }

}
