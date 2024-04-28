package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    boolean contains(Long userId);

    void deleteUser(Long userId);

    User fetchUser(Long userId);

    void updateUser(Long userId, UserDto user);

    Long addUser(User user);

    Collection<User> fetchAllUsers();
}
