package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> findAll();
    Film create(Film film);
    Film update(Film film);
    Optional<Film> getById(long id);
    int deleteById(long id);
    Optional<Film> addLike(long filmId, long userId);
    Optional<Film> removeLike(long filmId, long userId);
    List<Film> getBestFilms(int count, Integer genre, Integer year);
    List<Film> getAllByDirector(long directorId, String sortBy);
    List<Film> getCommonFilms(long userId, long friendId);
    List<Film> searchUsingKeyWord(String query, String by);
    List<Film> getRecommendedFilms(long userId);
}
