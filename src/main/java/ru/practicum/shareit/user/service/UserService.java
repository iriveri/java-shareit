package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    User create(User newUser);

    User edit(Long userId, User userDto);

    User getById(Long userId);

    void delete(Long userId);

    Collection<User> getAllUsers();

    void validate(Long userId);
}
