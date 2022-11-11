package ru.yandex.practicum.filmorate.storage;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InternalException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private static final Map<Integer, User> users = new HashMap<>();
    @Setter
    private int userId = 1;

    @Override
    public Map<Integer, User> getUsers() {
        return users;
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        validate(user);
        checkUsers(user);
        user.setId(userId++);
        log.info("Добавлен пользователь с логином {}", user.getLogin());
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User update(User user) {
        if (!getUsers().containsKey(user.getId()))
            throw new ObjectNotFoundException("Пользователя не существует, необходима регистрация нового пользователя");
        validate(user);
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

    public void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
    }

    public void checkUsers(User user) {
        if (findAll().stream().anyMatch(us -> us.getLogin().equals(user.getLogin())
                || us.getEmail().equals(user.getEmail())))
            throw new InternalException("Пользователь с таким email или login уже существует");
    }

}
