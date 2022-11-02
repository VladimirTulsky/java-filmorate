package ru.yandex.practicum.catsgram.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.develop.User;

import java.util.ArrayList;
import java.util.HashMap;

@RequestMapping("/home")
public class UserController {
    private HashMap<Integer, User> users = new HashMap<>();

    @GetMapping("/users")
    public HashMap<Integer, User> getUsers() {
        return users;
    }

    @PostMapping(value = "/users")
    public User createUser(@RequestBody User user) {
        for (User userok : users.values()) {
            if (userok.getEmail().equals(user.getEmail())) {
                try {
                    throw new UserAlreadyExistException("Такой майл уже существует");
                } catch (UserAlreadyExistException e) {
                    throw new RuntimeException(e);
                }
            } else if (user.getEmail() == null && user.getEmail().equals("")) {
                try {
                    throw new InvalidEmailException("Майл пустой");
                } catch (InvalidEmailException e) {
                    throw new RuntimeException(e);
                }
            } else {
                users.put(users.size(), user);
            }
        }
        return user;
    }

    @PutMapping("/users")
    public User puttis(@RequestBody User user) {
        if (user.getEmail() == null && user.getEmail().equals("")) {
            try {
                throw new InvalidEmailException("майл пустой");
            } catch (InvalidEmailException e) {
                throw new RuntimeException(e);
            }
        } else if (users.containsValue(user)) {
            users.put(users.size(), user);
        }
        return user;
    }
}
