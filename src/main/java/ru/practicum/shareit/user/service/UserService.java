package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto addNewUser(UserDto newUser);
    UserDto editUser(Long userId, UserDto userDto);
    UserDto getUser(Long userId);
    void deleteUser(Long userId);

    Collection<UserDto> getAllUsers();
}
