package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static ru.yandex.practicum.filmorate.enums.EventType.LIKE;
import static ru.yandex.practicum.filmorate.enums.OperationType.ADD;
import static ru.yandex.practicum.filmorate.enums.OperationType.REMOVE;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmService {
    private static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final DirectorStorage directorStorage;
    private final GenreStorage genreStorage;
    private final FeedStorage feedStorage;

    public List<Film> findAll() {
        List<Film> films = filmStorage.findAll();
        genreStorage.loadGenres(films);
        directorStorage.loadDirectors(films);

        log.info("Список фильмов отправлен");

        return films;
    }

    public Film create(Film film) {
        validate(film);
        log.info("Фильм добавлен");

        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validate(film);
        log.info("Фильм {} обновлен", film.getId());

        return filmStorage.update(film);
    }

    public Film getById(int id) {
        Film film = filmStorage.getById(id).orElseThrow(() -> {
            log.warn("Фильм с идентификатором {} не найден.", id);
            throw new ObjectNotFoundException("Фильм не найден");
        });
        genreStorage.loadGenres(Collections.singletonList(film));
        directorStorage.loadDirectors(Collections.singletonList(film));
        log.info("Фильм с id {} отправлен", id);

        return film;
    }

    public Film deleteById(int id) {
        log.info("Фильм {} удален", id);

        return filmStorage.deleteById(id).orElseThrow(() -> {
            log.warn("Фильм не найден");
            throw new ObjectNotFoundException("Фильм не найден");
        });
    }

    public Film addLike(int filmId, int userId) {
        if (filmStorage.getById(filmId).isEmpty() || userStorage.getById(userId).isEmpty()) {
            log.warn("Пользователь c id {} или фильм с id {} не найден.", userId, filmId);
            throw new ObjectNotFoundException("Пользователь или фильм не найдены");
        }
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
        feedStorage.addFeed(filmId, userId, Instant.now().toEpochMilli(), LIKE, ADD);
        return filmStorage.addLike(filmId, userId).orElseThrow();
    }

    public Film removeLike(int filmId, int userId) {
        if (userStorage.getById(userId).isEmpty()) {
            log.warn("Пользователь {} не найден.", userId);
            throw new ObjectNotFoundException("Пользователь не найден");
        }
        feedStorage.addFeed(filmId, userId, Instant.now().toEpochMilli(), LIKE, REMOVE);
        log.info("Пользователь {} удалил лайк к фильму {}", userId, filmId);

        return filmStorage.removeLike(filmId, userId).orElseThrow(() -> {
            log.warn("Фильм {} не найден.", filmId);
            throw new ObjectNotFoundException("Фильм не найден");
        });
    }

    public List<Film> getBestFilms(int count, Integer genreId, Integer year) {
        log.info("Запрошен список популярных фильмов. " +
                "Параметры: count={}, genreId={}, year={}", count, genreId, year);
        List<Film> films = filmStorage.getBestFilms(count, genreId, year);
        genreStorage.loadGenres(films);
        directorStorage.loadDirectors(films);

        return films;
    }

    public List<Film> getAllByDirector(int directorId, String sortBy) {
        log.info("Отправлен список фильмов, отсортированный по {} ", sortBy);
        directorStorage.getById(directorId).orElseThrow(() -> {
            log.warn("Режиссер с id {} не найден", directorId);
            throw new ObjectNotFoundException("Режиссер не найден");
        });
        List<Film> films = filmStorage.getAllByDirector(directorId, sortBy);
        genreStorage.loadGenres(films);
        directorStorage.loadDirectors(films);

        return films;
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        userStorage.getById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", userId);
            throw new ObjectNotFoundException("Пользователь не найден");
        });
        userStorage.getById(friendId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", friendId);
            throw new ObjectNotFoundException("Пользователь не найден");
        });
        log.info("Список общих фильмов отправлен");
        List<Film> films = filmStorage.getCommonFilms(userId, friendId);
        genreStorage.loadGenres(films);
        directorStorage.loadDirectors(films);

        return films;
    }

    public List<Film> searchUsingKeyWord(String query, String by) {
        log.info("Начинаем поиск по слову {}", query);
        List<Film> films = filmStorage.searchUsingKeyWord(query, by);
        genreStorage.loadGenres(films);
        directorStorage.loadDirectors(films);
        return films;
    }

    private void validate(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_FILM_DATE))
            throw new ValidationException("В то время кино еще не было");
    }
}