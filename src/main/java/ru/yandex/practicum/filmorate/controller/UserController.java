package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private int userId = 1;
    protected final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        validate(user);
        user.setId(userId++);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь с логином {}", user.getLogin());
        return user;
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId()))
            throw new ValidationException("Пользователя не существует, необходима регистрация нового пользователя");
        users.remove(user.getId());
        validate(user);
        users.put(user.getId(), user);
        log.info("Информация о пользователе {} обновлена", user.getLogin());
        return user;
    }

     void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
        Collection<User> userCollection = users.values();
        if (userCollection.stream().anyMatch(us -> us.getLogin().equals(user.getLogin())
                || us.getEmail().equals(user.getEmail())))
            throw new ValidationException("Пользователь с таким email или login уже существует");
    }
}