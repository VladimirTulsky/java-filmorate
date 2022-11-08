package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    protected final Map<Integer, User> users = new HashMap<>();

    private int userId = 1;

    UserService userService;

    public InMemoryUserStorage(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        userService.validate(user);
        checkUsers(user);
        user.setId(userId++);

        users.put(user.getId(), user);
        log.info("Добавлен пользователь с логином {}", user.getLogin());
        return user;
    }

    @Override
    public User put(User user) {
        if (!users.containsKey(user.getId()))
            throw new ValidationException("Пользователя не существует, необходима регистрация нового пользователя");
        userService.validate(user);
        users.put(user.getId(), user);
        log.info("Информация о пользователе {} обновлена", user.getLogin());
        return user;
    }

    @Override
    public User getById(int id) {
        return users.get(id);
    }

    @Override
    public User deleteById(int id) {
        User user = users.get(id);
        users.remove(id);
        return user;
    }

    private void checkUsers(User user) {
        if (users.values().stream().anyMatch(us -> us.getLogin().equals(user.getLogin())
                || us.getEmail().equals(user.getEmail())))
            throw new ValidationException("Пользователь с таким email или login уже существует");
    }
}
