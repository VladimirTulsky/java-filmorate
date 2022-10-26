package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private int idCounter = 1;
    Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Количество пользователей {}", users.size() );
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        validate(user);
        checkUsers(user);
        if (users.containsKey(user.getId()))
            throw new ValidationException("Такой пользователь уже существует");

        users.put(idCounter++, user);
        return user;
    }

    @PutMapping
    public User put(@RequestBody User user) {
        validate(user);
        if (!users.containsKey(user.getId())) throw new ValidationException("Пользвоателя не существует");
        users.remove(user.getId());
        checkUsers(user);
        users.put(user.getId(), user);
        return user;
    }

    private void validate(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@"))
            throw new ValidationException("Адрес электронной почты не может быть пустым.");
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }
        if (user.getName().isBlank()) user.setName(user.getLogin());
        if (user.getBirthday().isAfter(LocalDate.now()))
            throw new ValidationException("Ты еще не родился, пшел вон");
    }

    private void checkUsers(@RequestBody User user) {
        Collection<User> userCollection = users.values();
        for (User user1 : userCollection) {
            if (user.getLogin().equals(user1.getLogin()) || user.getEmail().equals(user1.getEmail()) ) {
                throw new ValidationException("Пользователь с таким email или login уже существует");
            }
        }
    }
}
