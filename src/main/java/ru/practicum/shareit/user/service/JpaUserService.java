package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserJpaRepository;

import javax.transaction.Transactional;
import java.util.Collection;

@Service
@Qualifier("JpaUserService")
public class JpaUserService implements UserService {

    private final UserJpaRepository userRepository;

    @Autowired
    public JpaUserService(UserJpaRepository userRepository) {
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
        if (newInfo.getName() != null) {
            existingUser.setName(newInfo.getName());
        }
        if (newInfo.getEmail() != null) {
            existingUser.setEmail(newInfo.getEmail());
        }
        return existingUser;
    }

    @Override
    public User getUserById(Long userId) {
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
