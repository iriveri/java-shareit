package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto create(UserDto newUser);

    UserDto edit(Long userId, UserDto userDto);

    UserDto getUserById(Long userId);

    void delete(Long userId);

    Collection<UserDto> getAllUsers();

    void validate(Long userId);
}
