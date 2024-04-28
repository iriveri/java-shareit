package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;

@Service
public interface UserService {
    UserDto addNewUser(UserDto newUser);
    UserDto editUser(Long userId, UserDto userDto);
    UserDto getUser(Long userId);
    UserDto deleteUser(Long userId);
}
