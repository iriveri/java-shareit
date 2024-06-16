package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService service;
    private final UserMapper mapper;

    @Autowired
    public UserController(UserService service, UserMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    /**
     * Создание нового пользователя.
     * Endpoint: POST /users
     * Принимает объект {@link UserDto} в теле запроса.
     *
     * @param newUser объект {@link UserDto}, представляющий нового пользователя
     * @return {@link ResponseEntity} содержащий созданный объект {@link UserDto} и статус ответа {@link HttpStatus#CREATED}
     */
    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @Valid @RequestBody UserDto newUser
    ) {
        var user = mapper.toUser(newUser);
        user = service.create(user);
        log.info("New user is created with ID {}", user.getId());
        var userToTransfer = mapper.toDto(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userToTransfer);
    }

    /**
     * Редактирование пользователя.
     * Endpoint: PATCH /users/{userId}
     * Позволяет изменить информацию о пользователе.
     *
     * @param userId  идентификатор пользователя, данные которого необходимо изменить
     * @param userDto объект {@link UserDto}, содержащий измененные данные пользователя
     * @return {@link ResponseEntity} содержащий обновленный объект {@link UserDto} и статус ответа {@link HttpStatus#OK}
     */
    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> editUser(
            @PathVariable Long userId,
            @RequestBody UserDto userDto
    ) {
        var user = mapper.toUser(userDto);
        user = service.edit(userId, user);
        log.info("User data for ID {} has been successfully patched", userId);
        var userToTransfer = mapper.toDto(user);
        return ResponseEntity.status(HttpStatus.OK).body(userToTransfer);
    }

    /**
     * Просмотр информации о конкретном пользователе.
     * Endpoint: GET /users/{userId}
     *
     * @param userId идентификатор пользователя, информацию о котором необходимо получить
     * @return {@link ResponseEntity} содержащий объект {@link UserDto} с данными пользователя и статус ответа {@link HttpStatus#OK}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(
            @PathVariable Long userId
    ) {
        var user = service.getById(userId);
        log.info("User data for ID {} has been successfully extracted", userId);
        var userToTransfer = mapper.toDto(user);
        return ResponseEntity.status(HttpStatus.OK).body(userToTransfer);
    }

    /**
     * Удаление пользователя.
     * Endpoint: DELETE /users/{userId}
     *
     * @param userId идентификатор пользователя, которого необходимо удалить
     * @return {@link ResponseEntity} со статусом ответа {@link HttpStatus#OK}
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long userId
    ) {
        service.delete(userId);
        log.info("User data for ID {} has been successfully deleted", userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Просмотр списка всех пользователей.
     * Endpoint: GET /users
     *
     * @return {@link ResponseEntity} содержащий список объектов {@link UserDto} и статус ответа {@link HttpStatus#OK}
     */
    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        var users = service.getAllUsers();
        log.info("List consisting of {} users has been successfully fetched", users.size());
        var usersToTransfer = users.stream().map(mapper::toDto).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(usersToTransfer);
    }
}
