package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Collection<Film> findAll();
    Film create(Film film);
    Film put(Film film);
    Film getById(int id);
    Film deleteById(int id);
}
