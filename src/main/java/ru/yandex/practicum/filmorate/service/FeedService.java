package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FeedService {

    private final FeedStorage feedStorage;
    private final UserStorage userStorage;

    public List<Feed> getFeed(int id) {
        userStorage.getById(id).orElseThrow(()-> {
           log.warn("Пользователь не существует");
           throw new ObjectNotFoundException("Пользователь не существует");
        });
        log.info("Возвращаем ленту событий пользователя с ид {}", id);
        return feedStorage.getFeed(id);
    }
}
