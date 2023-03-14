package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DirectorService {
    private final DirectorStorage directorStorage;

    public List<Director> findAll() {
        log.info("Список режиссеров отправлен");

        return directorStorage.findAll();
    }

    public Director create(Director director) {
        log.info("Режиссер добавлен в базу");

        return directorStorage.create(director);
    }

    public Director update(Director director) {
        log.info("Режиссер с id {} обновлен", director.getId());

        return directorStorage.update(director);
    }

    public Director getById(long id) {
        log.info("Режиссер с id {} отправлен", id);

        return directorStorage.getById(id).orElseThrow(() -> {
            log.warn("Режиссер с идентификатором {} не найден.", id);
            throw new ObjectNotFoundException("Режиссер не найден");
        });
    }

    public void deleteById(long id) {
        log.info("Удалить режиссера с id {}", id);
        int result = directorStorage.deleteById(id);
        if (result == 0) throw new ObjectNotFoundException("Режиссер не найден");
    }
}
