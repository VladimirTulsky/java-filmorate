package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.HashMap;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    HashMap<Integer, User> users = new HashMap<>();
    private int id = 0;

    private int makeId() {
        return id++;
    }

    @GetMapping("/users")
    public HashMap<Integer, User> getUsers() {
        log.info("Получен /GET запрос о выводе пользователей");
        return users;
    }

    @PostMapping(value = "/user/create")
    public User createUser(@Valid @RequestBody User user) {
        log.info("Получен /POST запрос создание пользователя");
        try {
            validateName(user);
            validateLogin(user);
            log.info("Проверка наличия в списке");
            if (users.containsKey(user.getId())) {
                log.info("Такой пользователь уже существует");
                throw new ValidationException("Такой пользователь уже существует");
            } else {
                users.put(user.getId(), user);
                log.info("Пользователь с именем " + user.getName() + "создан");
            }
        } catch (Exception | ValidationException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @PutMapping(value = "/user/update")
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Получен /POST запрос обновление пользователя");
        try {
            validateName(user);
            validateLogin(user);
            log.info("Проверка наличия в списке");
            if (users.containsKey(user.getId())) {
                users.put(user.getId(), user);
                log.info("Пользователь обновлён");
            } else {
                log.info("Пользователя с таким ид не существует");
                throw new ValidationException("Пользователя с таким ид не существует");
            }
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    private User validateName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Присваиваем поле login '{}' для поля name '{}' ", user.getLogin(), user.getName());
            user.setName(user.getLogin());
        }
        return user;
    }

    private User validateLogin(User user) throws ValidationException {
        if (user.getLogin().contains(" ")) {
            log.info("Пробел в поле login");
            throw new ValidationException("В поле логин не должно быть пробелов");
        }
        return user;
    }
}

