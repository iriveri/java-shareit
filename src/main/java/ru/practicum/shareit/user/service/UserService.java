package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto addNewUser(UserDto newUser);
    UserDto editUser(Long userId, UserDto userDto);
    UserDto getUser(Long userId);
    Boolean deleteUser(Long userId);
}
