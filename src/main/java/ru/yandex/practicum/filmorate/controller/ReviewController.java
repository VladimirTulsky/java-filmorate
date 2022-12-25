package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        return reviewService.create(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    public Review deleteById(@PathVariable int id) {
        return reviewService.deleteById(id);
    }

    @GetMapping("/{id}")
    public Review getById(@PathVariable int id) {
        return reviewService.getById(id);
    }

    @GetMapping
    public List<Review> findAll(@RequestParam(required = false, defaultValue = "-1") int filmId, @RequestParam(required = false, defaultValue = "10") int count) {
        return reviewService.findAll(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public Review addLike(@PathVariable int id, @PathVariable int userId) {
        return reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public Review addDislike(@PathVariable int id, @PathVariable int userId) {
        return reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Review removeLike(@PathVariable int id, @PathVariable int userId) {
        return reviewService.removeLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public Review removeDislike(@PathVariable int id, @PathVariable int userId) {
        return reviewService.removeDislike(id, userId);
    }

}
