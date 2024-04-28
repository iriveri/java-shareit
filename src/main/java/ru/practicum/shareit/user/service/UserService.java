package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

@Service
public interface UserService {
    UserDto addNewUser(UserDto newUser);
    UserDto editUser(Long userId, UserDto userDto);
    UserDto getUser(Long userId);
    UserDto deleteUser(Long userId);
}
