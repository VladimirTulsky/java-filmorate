package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmValidationTests {
    Film film;
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void rightFieldsValidationTest() {
        film = new Film(1, "Film", "Something about good story", LocalDate.of(2021, 1, 2), 120, null, null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    void nullNameValidationTest() {
        film = new Film(1, null, "Something about good story", LocalDate.of(2021, 1, 2), 120, null, null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void blankNameValidationTest() {
        film = new Film(1, " ", "Something", LocalDate.of(1896, 1, 2), 60, null, null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void nullDescriptionTest() {
        film = new Film(1, "Film", null, LocalDate.of(2021, 1, 2), 120, null, null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void emptyDescriptionTest() {
        film = new Film(1, "Film", "", LocalDate.of(2021, 1, 2), 120, null, null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void descriptionBiggerThen200Test() {
        film = new Film(1, "Film", "Something about a strange story starring Brad Pitt, where he finds " +
                "himself in Smolensk and does not understand what the hell is going on with his life and why everything is " +
                "so depressing around. But he finds a way out of the situation and moves to Kaluga."
                , LocalDate.of(2021, 1, 2), 120, null, null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void durationLessThen1MinuteTest() {
        film = new Film(1, "Film", "Something about good story", LocalDate.of(2021, 1, 2), 0, null, null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }
}
