package ru.practicum.shareit.server.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.common.user.model.User;
import ru.practicum.shareit.server.user.storage.UserJpaRepository;

import javax.transaction.Transactional;
import java.util.Collection;

@Service
@Qualifier("UserServiceImpl")
public class UserServiceImpl implements UserService {

    private final UserJpaRepository userRepository;

    @Autowired
    public UserServiceImpl(UserJpaRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User newUser) {
        return userRepository.save(newUser);
    }

    @Override
    @Transactional
    public User edit(Long userId, User newInfo) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        updateUserInfo(existingUser, newInfo);
        return existingUser;
    }

    private void updateUserInfo(User existingUser, User newInfo) {
        if (newInfo.getName() != null) {
            existingUser.setName(newInfo.getName());
        }
        if (newInfo.getEmail() != null) {
            existingUser.setEmail(newInfo.getEmail());
        }
    }

    @Override
    public User getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public Collection<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void validate(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
    }
}
