package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable int id) {
        return userService.getById(id);
    }

    @DeleteMapping("/{id}")
    public User deleteById(@PathVariable int id) {
        return userService.deleteById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public List<Integer> addFriend(@PathVariable int id, @PathVariable int friendId) {
        return userService.followUser(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public List<Integer> removeFriend(@PathVariable int id, @PathVariable int friendId) {
        return userService.unfollowUser(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendsList(@PathVariable int id) {
        return userService.getFriendsListById(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.getCommonFriendsList(id, otherId);
    }
}