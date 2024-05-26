package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserMapper mapper;

    @Autowired
    public UserServiceImpl(UserStorage userStorage, UserMapper mapper) {
        this.userStorage = userStorage;
        this.mapper = mapper;
    }

    @Override
    public UserDto create(UserDto newUser) {
        User user = mapper.dtoUserToUser(newUser);
        long id = userStorage.addUser(user);
        return getUserById(id);
    }

    @Override
    public void validate(Long userId) {
        if (!userStorage.contains(userId)) {
            throw new NotFoundException("User with this ID doesnt exist");
        }
    }

    @Override
    public UserDto edit(Long userId, UserDto userDto) {
        validate(userId);
        userStorage.updateUser(userId, userDto);
        return getUserById(userId);
    }

    @Override
    public UserDto getUserById(Long userId) {
        validate(userId);
        return mapper.userToUserDto(userStorage.fetchUser(userId));
    }

    @Override
    public void delete(Long userId) {
        validate(userId);
        userStorage.deleteUser(userId);
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return userStorage.fetchAllUsers()
                .stream()
                .map(mapper::userToUserDto)
                .collect(Collectors.toList());
    }
}