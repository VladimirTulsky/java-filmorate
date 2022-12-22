package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmService {
    private static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmDbStorage;
    private final UserStorage userDbStorage;
    private final DirectorStorage directorStorage;
    private final GenreStorage genreStorage;

    public List<Film> findAll() {
        List<Film> films = filmDbStorage.findAll();
        genreStorage.loadGenres(films);
        directorStorage.loadDirectors(films);

        log.info("Список фильмов отправлен");

        return films;
    }

    public Film create(Film film) {
        validate(film);
        log.info("Фильм добавлен");

        return filmDbStorage.create(film);
    }

    public Film update(Film film) {
        validate(film);
        log.info("Фильм {} обновлен", film.getId());

        return filmDbStorage.update(film);
    }

    public Film getById(int id) {
        Film film = filmDbStorage.getById(id).orElseThrow(() -> {
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

        return filmDbStorage.deleteById(id).orElseThrow(() -> {
            log.warn("Фильм не найден");
            throw new ObjectNotFoundException("Фильм не найден");
        });
    }

    public Film addLike(int filmId, int userId) {
        if (filmDbStorage.getById(filmId).isEmpty() || userDbStorage.getById(userId).isEmpty()) {
            log.warn("Пользователь c id {} или фильм с id {} не найден.", userId, filmId);
            throw new ObjectNotFoundException("Пользователь или фильм не найдены");
        }
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);

        return filmDbStorage.addLike(filmId, userId).orElseThrow();
    }

    public Film removeLike(int filmId, int userId) {
        if (userDbStorage.getById(userId).isEmpty()) {
            log.warn("Пользователь {} не найден.", userId);
            throw new ObjectNotFoundException("Пользователь не найден");
        }
        log.info("Пользователь {} удалил лайк к фильму {}", userId, filmId);

        return filmDbStorage.removeLike(filmId, userId).orElseThrow(() -> {
            log.warn("Фильм {} не найден.", filmId);
            throw new ObjectNotFoundException("Фильм не найден");
        });
    }

    public List<Film> getBestFilms(int count) {
        log.info("Отправлен список из {} самых популярных фильмов", count);
        List<Film> films = filmDbStorage.getBestFilms(count);
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

        List<Film> films = filmDbStorage.getAllByDirector(directorId, sortBy);
        genreStorage.loadGenres(films);
        directorStorage.loadDirectors(films);

        return films;
    }

    private void validate(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_FILM_DATE))
            throw new ValidationException("В то время кино еще не было");
    }
}