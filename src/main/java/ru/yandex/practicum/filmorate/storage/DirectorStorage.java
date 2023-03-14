package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {
    List<Director> findAll();
    Director create(Director director);
    Director update(Director director);
    Optional<Director> getById(long id);
    int deleteById(long id);
    void loadDirectors(List<Film> films);
}
