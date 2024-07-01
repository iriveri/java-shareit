package ru.practicum.gateway.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.user.dto.UserDto;
import ru.practicum.gateway.user.service.UserService;

import javax.validation.Valid;


@RestController
@RequestMapping("/users")
@Slf4j
@Validated
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @Valid @RequestBody UserDto newUser
    ) {
        var user = service.create(newUser);
        log.info("New user is created with ID {}", user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> editUser(
            @PathVariable Long userId,
            @RequestBody UserDto user
    ) {
        user = service.edit(userId, user);
        log.info("User data for ID {} has been successfully patched", userId);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(
            @PathVariable Long userId
    ) {
        var user = service.getById(userId);
        log.info("User data for ID {} has been successfully extracted", userId);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long userId
    ) {
        service.delete(userId);
        log.info("User data for ID {} has been successfully deleted", userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        var users = service.getAllUsers();
        log.info("List consisting of {} users has been successfully fetched", users.size());
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }
}
