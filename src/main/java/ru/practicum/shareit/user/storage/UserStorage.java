package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public interface UserStorage {
    boolean contains(Long userId);

    Boolean deleteUser(Long userId);

    User fetchUser(Long userId);

    User updateUser(User user);

    User addUser(User user);
}
