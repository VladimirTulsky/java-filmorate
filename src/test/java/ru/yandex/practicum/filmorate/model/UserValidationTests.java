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

public class UserValidationTests {
    User user;
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void rightFieldsValidationTest() {
        user = new User("test@test.ru", "login", LocalDate.of(1990, 5, 6));
        user.setName("Vladimir");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void wrongEmailValidationTest() {
        user = new User("testtest.ru", "login", LocalDate.of(1990, 5, 6));
        user.setName("Vladimir");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void emptyEmailValidationTest() {
        user = new User(null, "login", LocalDate.of(1990, 05, 06));
        user.setName("Vladimir");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void nullLoginValidationTest() {
        user = new User("test@test.ru", null, LocalDate.of(1990, 5, 6));
        user.setName("Vladimir");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void emptyLoginValidationTest() {
        user = new User("test@test.ru", "", LocalDate.of(1990, 5, 6));
        user.setName("Vladimir");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(2);
    }

    @Test
    void loginWithSpacesValidationTest() {
        user = new User("test@test.ru", "log in", LocalDate.of(1990, 5, 6));
        user.setName("Vladimir");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void withoutBirthdayValidationTest() {
        user = new User("test@test.ru", "login", null);
        user.setName("Vladimir");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }
}
