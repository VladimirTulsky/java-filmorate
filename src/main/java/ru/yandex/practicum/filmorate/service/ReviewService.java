package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewService {

    private final ReviewStorage reviewDbStorage;
    private final FilmStorage filmDbStorage;
    private final UserStorage userDbStorage;

    public Review create(Review review) {
        if (review.getUserId() == 0 || review.getFilmId() == 0) {
            log.warn("Не выбран пользователь или фильм");
            throw new ValidationException("Пользователь или фильм не указаны");
        }
        if (userDbStorage.getById(review.getUserId()).isEmpty() || filmDbStorage.getById(review.getFilmId()).isEmpty()) {
            log.warn("Пользователь c id {} или фильм с id {} не найден", review.getUserId(), review.getFilmId());
            throw new ObjectNotFoundException("Пользователь или фильм не найдены");
        }
        log.info("Отзыв добавлен в базу");

        return reviewDbStorage.create(review);
    }

    public Review update(Review review) {
        log.info("Отзыв с id {} обновлен", review.getReviewId());

        return reviewDbStorage.update(review).orElseThrow(() -> {
            log.warn("Отзыв не найден");
            throw new ObjectNotFoundException("Отзыв не найден");
        });
    }

    public Review deleteById(int id) {
        return reviewDbStorage.deleteById(id).orElseThrow(() -> {
            log.warn("Отзыв с идентификатором {} не найден", id);
            throw new ObjectNotFoundException("Отзыв не найден");
        });
    }

    public Review getById(int id) {
        return reviewDbStorage.getById(id).orElseThrow(() -> {
            log.warn("Отзыв с идентификатором {} не найден", id);
            throw new ObjectNotFoundException("Отзыв не найден");
        });
    }

    public List<Review> findAll(int filmId, int count) {
        List<Review> reviews = reviewDbStorage.getReviewsByFilmId(filmId, count);
        log.info(String.format("Список %d популярных отзывов: %s", count, reviews));

        return reviews;
    }

    public Review addLike(int id, int userId) {
        if (!reviewDbStorage.contains(id, userId, true)) {
            if (!reviewDbStorage.contains(id, userId, false)) {
                reviewDbStorage.updateUseful(id, 1);
            } else {
                reviewDbStorage.updateUseful(id, 2);
            }
            reviewDbStorage.addLike(id, userId, true);
        }

        return getById(id);
    }

    public Review addDislike(int id, int userId) {
        if (!reviewDbStorage.contains(id, userId, false)) {
            if (!reviewDbStorage.contains(id, userId, true)) {
                reviewDbStorage.updateUseful(id, -1);
            } else {
                reviewDbStorage.updateUseful(id, -2);
            }
            reviewDbStorage.addLike(id, userId, false);
        }

        return getById(id);
    }

    public Review removeLike(int id, int userId) {
        if (reviewDbStorage.contains(id, userId, true)) {
            reviewDbStorage.removeLike(id, userId, true);
            reviewDbStorage.updateUseful(id, -1);
        }

        return getById(id);
    }

    public Review removeDislike(int id, int userId) {
        if (reviewDbStorage.contains(id, userId, false)) {
            reviewDbStorage.removeLike(id, userId, false);
            reviewDbStorage.updateUseful(id, 1);
        }

        return getById(id);
    }

}
