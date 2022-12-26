package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> findAll();

    User create(User user);

    User update(User user);

    Optional<User> getById(int id);

    Optional<User> deleteById(int id);

    List<Integer> followUser(int followingId, int followerId);

    List<Integer> unfollowUser(int followingId, int followerId);

    List<User> getFriendsListById(int id);

    List<User> getCommonFriendsList(int firstId, int secondId);
}
