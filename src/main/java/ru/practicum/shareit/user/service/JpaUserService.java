package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserJpaRepository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Qualifier("JpaUserService")
public class JpaUserService implements UserService {

    private final UserJpaRepository userRepository;
    private final UserMapper mapper;

    @Autowired
    public JpaUserService(UserJpaRepository userRepository, UserMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public UserDto create(UserDto newUser) {
        User user = mapper.dtoUserToUser(newUser);
        user = userRepository.save(user);
        return mapper.userToUserDto(user);
    }

    @Override
    @Transactional
    public UserDto edit(Long userId, UserDto userDto) {
        User existingUser = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        existingUser.setName(userDto.getName());
        existingUser.setEmail(userDto.getEmail());
        return mapper.userToUserDto(existingUser);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User existingUser = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return mapper.userToUserDto(existingUser);
    }

    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(mapper::userToUserDto).collect(Collectors.toList());
    }

    @Override
    public void validate(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("Item not found");
        }
    }
}
