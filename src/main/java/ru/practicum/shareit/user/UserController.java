package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserService service;
    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto newUser) {
        var user = service.addNewUser(newUser);
        log.info("New user is created with ID {}", 1);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> editUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        var user = service.editUser(userId , userDto);
        log.info("User data for ID {} has been successfully patched",userId);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long userId) {
        var user = service.getUser(userId);
        log.info("User data for ID {} has been successfully extracted",userId);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
    @DeleteMapping("/{userId}")
    public ResponseEntity<UserDto> deleteUser(@PathVariable Long userId) {
        var user = service.deleteUser(userId);
        log.info("User with ID {} has been successfully deleted",userId);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }


}
