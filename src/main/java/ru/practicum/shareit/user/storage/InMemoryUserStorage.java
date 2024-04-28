package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryUserStorage implements UserStorage {
    private static long genId = 0;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        if (users.values().stream().map(User::getEmail).anyMatch(user.getEmail()::equals)) {
            throw new ValidationException("Пользователь с электронной почтой " +
                    user.getEmail() + " уже зарегистрирован.");
        }
        user.setId(++genId);
        users.put(user.getId(), user);
        return user;
    }
    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new IllegalArgumentException("Пользователь с Id: " + user.getId() + " не найден в списке!");
        }
        users.put(user.getId(), user);
        return user;
    }
    @Override
    public User fetchUser(Long userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        }
        throw new IllegalArgumentException("Пользователь с Id " + userId + " не найден");
    }
    @Override
    public Boolean deleteUser(Long id) {
        if (!users.containsKey(id)) {
            throw new IllegalArgumentException("Такого пользователя не существует!");
        }
        users.remove(id);
        return true;
    }

    @Override
    public boolean contains(Long userId) {
        return users.containsKey(userId);
    }

}