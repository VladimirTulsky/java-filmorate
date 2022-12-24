package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewDBStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review create(Review review) {
        return null;
    }

    @Override
    public Review update(Review review) {
        return null;
    }

    @Override
    public Optional<Review> deleteById(int id) {
        return Optional.empty();
    }

    @Override
    public Optional<Review> getById(int id) {
        return Optional.empty();
    }

    @Override
    public List<Review> getReviewsByFilmId(int filmId, int count) {
        return null;
    }

    @Override
    public Optional<Review> addLike(int reviewId, int userId, boolean like) {
        return Optional.empty();
    }

    @Override
    public Optional<Review> removeLike(int reviewId, int userId, boolean like) {
        return Optional.empty();
    }

    @Override
    public Boolean contains(int id, int userId, boolean isLike) {
        return null;
    }

    @Override
    public void updateUseful(int id, int likeCount) {

    }
}
