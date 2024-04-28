package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

@Service
public class BasicUserService implements UserService {
    private final UserStorage userStorage;
    private final UserMapper maper;

    @Autowired
    public BasicUserService(UserStorage userStorage) {
        this.userStorage = userStorage;
        this.maper = new UserMapperImpl();
    }

    @Override
    public UserDto addNewUser(UserDto newUser) {
        User user = maper.dtoItemToItem(newUser);
        validate(user);
        return maper.itemToItemDto(userStorage.addUser(user));
    }

    private void validate(User user) {
        if(user.getName().isEmpty()){
            throw new RuntimeException();
        }
    }

    @Override
    public UserDto editUser(Long userId, UserDto userDto) {
        User user = maper.dtoItemToItem(userDto);
        validate(user);
        if (!userStorage.contains(userId)) {
            return maper.itemToItemDto(userStorage.updateUser(user));
        } else {
            throw new RuntimeException("User with this ID doesnt exist");
        }
    }

    @Override
    public UserDto getUser(Long userId) {
        if (userStorage.contains(userId)) {
            return maper.itemToItemDto(userStorage.fetchUser(userId));
        } else {
            throw new RuntimeException("User with this ID doesnt exist");
        }
    }

    @Override
    public Boolean deleteUser(Long userId) {
        if (userStorage.contains(userId)) {
            return userStorage.deleteUser(userId);
        } else {
            throw new RuntimeException("User with this ID doesnt exist");
        }
    }
}
