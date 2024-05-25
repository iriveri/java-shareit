package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public class DatabaseUserStorage implements UserStorage{
    @Override
    public boolean contains(Long userId) {

        return false;
    }

    @Override
    public void deleteUser(Long userId) {

    }

    @Override
    public User fetchUser(Long userId) {
        return null;
    }

    @Override
    public void updateUser(Long userId, UserDto user) {

    }

    @Override
    public Long addUser(User user) {
        return null;
    }

    @Override
    public Collection<User> fetchAllUsers() {
        return null;
    }
}
