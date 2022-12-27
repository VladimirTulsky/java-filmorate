package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DataException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review create(Review review) {
        String sql = "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"review_id"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setLong(3, review.getUserId());
            stmt.setLong(4, review.getFilmId());
            stmt.setInt(5, 0);
            return stmt;
        }, keyHolder);
        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        return review;
    }

    @Override
    public Optional<Review> update(Review review) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ?" +
                "WHERE REVIEW_ID = ?";
        int result = jdbcTemplate.update(sql,
                review.getContent(), review.getIsPositive(), review.getReviewId());
        if (result < 1) throw new DataException("Отзыв не найден в базе");

        return getById(review.getReviewId());
    }

    @Override
    public Optional<Review> deleteById(long id) {
        Optional<Review> review = getById(id);
        String sql = "DELETE FROM reviews WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sql, id);

        return review;
    }

    @Override
    public Optional<Review> getById(long id) {
        String sql = "SELECT * FROM reviews WHERE REVIEW_ID = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, ReviewDbStorage::makeReview, id));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Review> getReviewsByFilmId(long filmId, int count) {
        String sqlAll = "SELECT * FROM reviews ORDER BY USEFUL DESC LIMIT ?";
        String sql = "SELECT * FROM reviews WHERE FILM_ID=? ORDER BY USEFUL DESC LIMIT ?";
        if (filmId == -1) {
            return jdbcTemplate.query(sqlAll, ReviewDbStorage::makeReview, count);
        } else {
            return jdbcTemplate.query(sql, ReviewDbStorage::makeReview, filmId, count);
        }
    }

    @Override
    public void addLike(long reviewId, long userId, boolean like) {
        String sql = "MERGE INTO REVIEWS_LIKES (REVIEW_ID, USER_ID, IS_POSITIVE) VALUES(?,?,?)";
        jdbcTemplate.update(sql, reviewId, userId, like);
    }

    @Override
    public void removeLike(long reviewId, long userId, boolean like) {
        String sql = "DELETE FROM REVIEWS_LIKES WHERE REVIEW_ID=? AND USER_ID=? AND IS_POSITIVE=?";
        jdbcTemplate.update(sql, reviewId, userId, like);
    }

    @Override
    public Boolean contains(long id, long userId, boolean like) {
        String sql = "SELECT * FROM REVIEWS_LIKES WHERE REVIEW_ID=? AND USER_ID=? AND IS_POSITIVE=?";
        var rows = jdbcTemplate.queryForRowSet(sql, id, userId, like);
        return rows.isBeforeFirst();
    }

    @Override
    public void updateUseful(long id, int likeCount) {
        String sql = "UPDATE reviews SET USEFUL=USEFUL+? WHERE REVIEW_ID=?";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(sql);
            ps.setInt(1, likeCount);
            ps.setLong(2, id);
            return ps;
        });
    }

    static Review makeReview(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("review_id");
        String content = rs.getString("content");
        Boolean isPositive = rs.getBoolean("is_positive");
        long userId = rs.getLong("user_id");
        long filmId = rs.getLong("film_id");
        int useful = rs.getInt("useful");

        return new Review(id, content, isPositive, userId, filmId, useful);
    }
}
