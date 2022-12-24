package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    Review create(Review review);
    Review update(Review review);
    Optional<Review> deleteById(int id);
    Optional<Review> getById(int id);
    List<Review> getReviewsByFilmId(int filmId, int count);
    Optional<Review> addLike(int reviewId, int userId, boolean like);
    Optional<Review> removeLike(int reviewId, int userId, boolean like);

    Boolean contains(int id, int userId, boolean isLike);
    void updateUseful(int id, int likeCount);
}
