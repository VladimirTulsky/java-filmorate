package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.enums.EventType.FRIEND;
import static ru.yandex.practicum.filmorate.enums.OperationType.ADD;
import static ru.yandex.practicum.filmorate.enums.OperationType.REMOVE;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserService {
    private final UserStorage userDbStorage;
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;
    private final FeedStorage feedStorage;
    private static final int USER_MATCH_LIMIT = 20;

    public List<User> findAll() {
        log.info("Список пользователей отправлен");

        return userDbStorage.findAll();
    }

    public User create(User user) {
        validate(user);
        log.info("Пользователь добавлен");

        return userDbStorage.create(user);
    }

    public User update(User user) {
        validate(user);
        log.info("Пользователь {} обновлен", user.getId());

        return userDbStorage.update(user);
    }

    public User getById(int id) {
        log.info("Пользователь с id {} отправлен", id);

        return userDbStorage.getById(id).orElseThrow(() -> {
            log.warn("Пользователь с идентификатором {} не найден.", id);
            throw new ObjectNotFoundException("Пользователь не найден");
        });
    }

    public User deleteById(int id) {
        log.info("Пользователь {} удален", id);

        return userDbStorage.deleteById(id).orElseThrow(() -> {
            log.warn("Пользователь не найден");
            throw new ObjectNotFoundException("Пользователь не найден");
        });
    }

    public List<Integer> followUser(int followerId, int followingId) {
        usersValidation(followerId, followingId);
        log.info("Пользователь {} подписался на {}", followerId, followingId);
        feedStorage.addFeed(followingId, followerId, Instant.now().toEpochMilli(), FRIEND, ADD);
        return userDbStorage.followUser(followerId, followingId);
    }

    public List<Integer> unfollowUser(int followerId, int followingId) {
        log.info("Пользователь {} отписался от {}", followerId, followingId);
        feedStorage.addFeed(followingId, followerId, Instant.now().toEpochMilli(), FRIEND, REMOVE);
        return userDbStorage.unfollowUser(followerId, followingId);
    }

    public List<User> getFriendsListById(int id) {
        getById(id);
        log.info("Запрос получения списка друзей пользователя {} выполнен", id);

        return userDbStorage.getFriendsListById(id);
    }

    public List<User> getCommonFriendsList(int firstId, int secondId) {
        usersValidation(firstId, secondId);
        log.info("Список общих друзей {} и {} отправлен", firstId, secondId);

        return userDbStorage.getCommonFriendsList(firstId, secondId);
    }

    public List<Film> getFilmRecommendations(int userId) {
        log.info("Запрошены рекомендации для пользователя с идентификатором {}", userId);

        getById(userId);

        List<Integer> userFilms = filmStorage.getUserFilms(userId);
        Map<Integer, Integer> matches = userDbStorage.getUserMatches(userFilms, userId, USER_MATCH_LIMIT);

        if (matches.isEmpty()) {
            return new ArrayList<>();
        }

        int maxValue = Collections.max(matches.values());

        List<Integer> topUsers = matches.entrySet().stream()
                .filter(entry -> entry.getValue() == maxValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<Integer> recommendedFilmsIds = filmStorage.getUsersFilms(topUsers).stream()
                .filter(filmId -> !userFilms.contains(filmId))
                .collect(Collectors.toList());

        List<Film> recommendedFilms = filmStorage.getByIds(recommendedFilmsIds);
        genreStorage.loadGenres(recommendedFilms);
        directorStorage.loadDirectors(recommendedFilms);

        return recommendedFilms;
    }

    public void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
    }

    public void usersValidation(int followerId, int followingId) {
        getById(followerId);
        getById(followingId);
    }
}