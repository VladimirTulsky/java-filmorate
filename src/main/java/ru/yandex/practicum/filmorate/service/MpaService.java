package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.impl.MpaDbStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
public class MpaService {

    private final MpaDbStorage mpaDbStorage;

    @Autowired
    public MpaService(MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    public Collection<Mpa> findAll() {
        log.info("Список рейтингов отправлен");

        return mpaDbStorage.findAll();
    }

    public Optional<Mpa> getById(int id) {
        Optional<Mpa> mpa = mpaDbStorage.getById(id);
        if (mpa.isEmpty()) {
            log.warn("Рейтинг {} не найден.", id);
            throw new ObjectNotFoundException("Рейтинг не найден");
        }
        log.info("Рейтинг отправлен");

        return mpa;
    }
}
