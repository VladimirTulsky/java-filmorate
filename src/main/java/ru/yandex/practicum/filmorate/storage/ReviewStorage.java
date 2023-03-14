package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    Review create(Review review);
    Optional<Review> update(Review review);
    Optional<Review> deleteById(long id);
    Optional<Review> getById(long id);
    List<Review> getReviewsByFilmId(long filmId, int count);
    void addLike(long reviewId, long userId, boolean like);
    void removeLike(long reviewId, long userId, boolean like);
    Boolean contains(long id, long userId, boolean like);
    void updateUseful(long id, int likeCount);
}
