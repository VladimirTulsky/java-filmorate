package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> findAll();

    User create(User user);

    User update(User user);

    Optional<User> getById(long id);

    int deleteById(long id);

    List<Long> followUser(long followingId, long followerId);

    List<Long> unfollowUser(long followingId, long followerId);

    List<User> getFriendsListById(long id);

    List<User> getCommonFriendsList(long firstId, long secondId);
}
