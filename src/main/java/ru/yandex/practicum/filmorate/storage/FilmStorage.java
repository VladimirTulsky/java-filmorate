package ru.yandex.practicum.filmorate.storage;

import io.swagger.models.auth.In;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> findAll();
    Film create(Film film);
    Film update(Film film);
    Optional<Film> getById(int id);
    List<Film> getByIds(Collection<Integer> ids);
    Optional<Film> deleteById(int id);
    Optional<Film> addLike(int filmId, int userId);
    Optional<Film> removeLike(int filmId, int userId);
    List<Film> getBestFilms(int count, Integer genre, Integer year);
    List<Film> getAllByDirector(int count, String sortBy);
    List<Film> getCommonFilms(int userId, int friendId);
    List<Integer> getUserFilms(int userId);
    List<Integer> getUsersFilms(List<Integer> userIds);
    List<Film> searchUsingKeyWord(String query, String by);
}
