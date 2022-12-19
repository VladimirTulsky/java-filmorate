package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreService {

    private final GenreStorage genreDbStorage;

    public Collection<Genre> findAll() {
        log.info("Список жанров отправлен");

        return genreDbStorage.findAll();
    }

    public Genre getById(int id) {
        log.info("Жанр отправлен");

        return genreDbStorage.getById(id).orElseThrow(() -> {
            log.warn("Жанр {} не найден.", id);
            throw new ObjectNotFoundException("Жанр не найден");
        });
    }
}
