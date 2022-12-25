package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    Review create(Review review);
    Optional<Review> update(Review review);
    Optional<Review> deleteById(int id);
    Optional<Review> getById(int id);
    List<Review> getReviewsByFilmId(int filmId, int count);
    void addLike(int reviewId, int userId, boolean like);
    void removeLike(int reviewId, int userId, boolean like);
    Boolean contains(int id, int userId, boolean like);
    void updateUseful(int id, int likeCount);
}
