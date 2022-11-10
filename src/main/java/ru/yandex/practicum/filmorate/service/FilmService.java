package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> findAll() {
        log.info("Список фильмов отправлен");

        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film getById(int id) {
        if (!filmStorage.getFilms().containsKey(id))
            throw new ObjectNotFoundException("Фильм не найден");
        log.info("Фильм с id {} отправлен", id);

        return filmStorage.getById(id);
    }

    public Film deleteById(int id) {
        if (!filmStorage.getFilms().containsKey(id))
            throw new ObjectNotFoundException("Фильм не найден, невозможно удалить");
        log.info("Фильм с id {} удален", id);

        return filmStorage.deleteById(id);
    }

    public Film addLike(int filmId, int userId) {
        if (!filmStorage.getFilms().containsKey(filmId))
            throw new ObjectNotFoundException("Фильм не найден");
        filmStorage.getById(filmId).getUsersLikes().add(userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);

        return filmStorage.getById(filmId);
    }

    public Film removeLike(int filmId, int userId) {
        if (!filmStorage.getFilms().containsKey(filmId))
            throw new ObjectNotFoundException("Фильм не найден");
        if (!filmStorage.getById(filmId).getUsersLikes().contains(userId))
            throw new ObjectNotFoundException("Лайк от пользователя отсутствует");
        filmStorage.getById(filmId).getUsersLikes().remove(userId);
        log.info("Пользователь {} удалил лайк к фильму {}", userId, filmId);

        return filmStorage.getById(filmId);
    }

    public List<Film> getBestFilms(int count) {
        log.info("Список самых популярных фильмов отправлен");

        return filmStorage.findAll().stream()
                .sorted((o1, o2) -> Integer.compare(o2.getUsersLikes().size(), o1.getUsersLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}