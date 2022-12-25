package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.Instant;
import java.util.List;

import static ru.yandex.practicum.filmorate.enums.EventType.REVIEW;
import static ru.yandex.practicum.filmorate.enums.OperationType.*;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FeedStorage feedStorage;

    public Review create(Review review) {
        if (review.getUserId() == 0 || review.getFilmId() == 0) {
            log.warn("Не выбран пользователь или фильм");
            throw new ValidationException("Пользователь или фильм не указаны");
        }
        if (userStorage.getById(review.getUserId()).isEmpty() || filmStorage.getById(review.getFilmId()).isEmpty()) {
            log.warn("Пользователь c id {} или фильм с id {} не найден", review.getUserId(), review.getFilmId());
            throw new ObjectNotFoundException("Пользователь или фильм не найдены");
        }
        log.info("Отзыв добавлен в базу");
        review = reviewStorage.create(review);
        feedStorage.addFeed(review.getReviewId(), review.getUserId(), Instant.now().toEpochMilli(), REVIEW, ADD);
        return review;
    }

    public Review update(Review review) {
        log.info("Отзыв с id {} обновлен", review.getReviewId());
        review = reviewStorage.update(review).orElseThrow(() -> {
            log.warn("Отзыв не найден");
            throw new ObjectNotFoundException("Отзыв не найден");
        });
        feedStorage.addFeed(review.getReviewId(), review.getUserId(), Instant.now().toEpochMilli(), REVIEW, UPDATE);
        return review;
    }

    public Review deleteById(int id) {
        Review review = reviewStorage.deleteById(id).orElseThrow(() -> {
            log.warn("Отзыв с идентификатором {} не найден", id);
            throw new ObjectNotFoundException("Отзыв не найден");
        });
        feedStorage.addFeed(review.getReviewId(), review.getUserId(), Instant.now().toEpochMilli(), REVIEW, REMOVE);
        return review;
    }

    public Review getById(int id) {
        return reviewStorage.getById(id).orElseThrow(() -> {
            log.warn("Отзыв с идентификатором {} не найден", id);
            throw new ObjectNotFoundException("Отзыв не найден");
        });
    }

    public List<Review> findAll(int filmId, int count) {
        List<Review> reviews = reviewStorage.getReviewsByFilmId(filmId, count);
        log.info(String.format("Список %d популярных отзывов: %s", count, reviews));

        return reviews;
    }

    public Review addLike(int id, int userId) {
        if (!reviewStorage.contains(id, userId, true)) {
            if (!reviewStorage.contains(id, userId, false)) {
                reviewStorage.updateUseful(id, 1);
            } else {
                reviewStorage.updateUseful(id, 2);
            }
            reviewStorage.addLike(id, userId, true);
        }

        return getById(id);
    }

    public Review addDislike(int id, int userId) {
        if (!reviewStorage.contains(id, userId, false)) {
            if (!reviewStorage.contains(id, userId, true)) {
                reviewStorage.updateUseful(id, -1);
            } else {
                reviewStorage.updateUseful(id, -2);
            }
            reviewStorage.addLike(id, userId, false);
        }

        return getById(id);
    }

    public Review removeLike(int id, int userId) {
        if (reviewStorage.contains(id, userId, true)) {
            reviewStorage.removeLike(id, userId, true);
            reviewStorage.updateUseful(id, -1);
        }

        return getById(id);
    }

    public Review removeDislike(int id, int userId) {
        if (reviewStorage.contains(id, userId, false)) {
            reviewStorage.removeLike(id, userId, false);
            reviewStorage.updateUseful(id, 1);
        }

        return getById(id);
    }

}
