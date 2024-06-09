package ru.practicum.shareit.user.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Repository
@Qualifier("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private static long genId = 0;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Long addUser(User user) {
        String email = user.getEmail();
        if (users.values().stream().anyMatch(existingUser -> existingUser.getEmail().equals(email))) {
            throw new DuplicateException("User with email " + email + " is already registered.");
        }
        long id = (user.getId() == null) ? ++genId : user.getId();
        user.setId(id);
        users.put(id, user);
        return id;
    }

    @Override
    public Collection<User> fetchAllUsers() {
        return users.values();
    }

    @Override
    public void updateUser(Long userId, User updatedUser) {
        if (!contains(userId)) {
            throw new IllegalArgumentException("User with Id: " + userId + " not found in the list!");
        }
        String email = updatedUser.getEmail();
        if (email != null && users.values().stream().anyMatch(existingUser -> existingUser.getEmail().equals(email) && !Objects.equals(existingUser.getId(), userId))) {
            throw new DuplicateException("User with email " + email + " is already registered.");
        }
        User oldUser = users.get(userId);
        if (updatedUser.getName() != null) {
            oldUser.setName(updatedUser.getName());
        }
        if (email != null) {
            oldUser.setEmail(email);
        }
        users.put(userId, oldUser);
    }

    @Override
    public User fetchUser(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("User with Id " + userId + " not found");
        }
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        if (!users.containsKey(id)) {
            throw new IllegalArgumentException("User does not exist!");
        }
        users.remove(id);
    }

    @Override
    public boolean contains(Long userId) {
        return users.containsKey(userId);
    }
}
