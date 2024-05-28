package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    private final UserService service;

    private final UserMapper mapper;
    @Autowired
    public UserController(@Qualifier("JpaUserService") UserService service, UserMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto newUser) {
        var user = mapper.dtoUserToUser(newUser);
        user = service.create(user);
        log.info("New user is created with ID {}", 1);
        var userToTransfer = mapper.userToUserDto(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userToTransfer);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> editUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        var user = mapper.dtoUserToUser(userDto);
        user = service.edit(userId, user);
        log.info("User data for ID {} has been successfully patched", userId);
        var userToTransfer = mapper.userToUserDto(user);
        return ResponseEntity.status(HttpStatus.OK).body(userToTransfer);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long userId) {
        var user = service.getUserById(userId);
        log.info("User data for ID {} has been successfully extracted", userId);
        var userToTransfer = mapper.userToUserDto(user);
        return ResponseEntity.status(HttpStatus.OK).body(userToTransfer);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        service.delete(userId);
        log.info("User data for ID {} has been successfully deleted", userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        var users = service.getAllUsers();
        log.info("List consisting of {} users has been successfully fetched", users.size());
        var usersToTransfer = users.stream().map(mapper::userToUserDto).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(usersToTransfer);
    }


}
