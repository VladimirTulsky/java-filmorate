package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.OperationType;
import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedStorage {
    void addFeed(Long entityId, Long userId, long timeStamp, EventType eventType, OperationType operation);

    List<Feed> getFeed(long id);
}
