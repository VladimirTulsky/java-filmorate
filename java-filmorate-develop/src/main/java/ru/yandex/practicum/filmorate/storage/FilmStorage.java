package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> findAll();
    Film create(Film film);
    Film update(Film film);
    Optional<Film> getById(int id);
    Optional<Film> deleteById(int id);
    Optional<Film> addLike(int filmId, int userId);
    Optional<Film> removeLike(int filmId, int userId);
    List<Film> getBestFilms(int count);
    List<Film> getAllByDirector(int count, String sortBy);

    List<Film> searchUsingKeyWord(String query, String by);
}
