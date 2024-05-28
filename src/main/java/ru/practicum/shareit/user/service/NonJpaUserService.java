package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Qualifier("NonJpaUserService")
public class NonJpaUserService implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public NonJpaUserService(@Qualifier("InMemoryUserStorage") UserStorage  userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User create(User newUser) {
        long id = userStorage.addUser(newUser);
        return getUserById(id);
    }

    @Override
    public void validate(Long userId) {
        if (!userStorage.contains(userId)) {
            throw new NotFoundException("User with this ID doesnt exist");
        }
    }

    @Override
    public User edit(Long userId, User user) {
        validate(userId);
        userStorage.updateUser(userId, user);
        return getUserById(userId);
    }

    @Override
    public User getUserById(Long userId) {
        validate(userId);
        return userStorage.fetchUser(userId);
    }

    @Override
    public void delete(Long userId) {
        validate(userId);
        userStorage.deleteUser(userId);
    }

    @Override
    public Collection<User> getAllUsers() {
        return userStorage.fetchAllUsers();
    }
}
