package ru.practicum.shareit.gateway.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserJpaRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserJpaRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_ShouldSaveNewUser() {
        User newUser = new User();
        newUser.setName("John Doe");
        newUser.setEmail("john.doe@example.com");

        when(userRepository.save(any(User.class))).thenReturn(newUser);

        User savedUser = userService.create(newUser);

        assertNotNull(savedUser);
        assertEquals("John Doe", savedUser.getName());
        assertEquals("john.doe@example.com", savedUser.getEmail());
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    void edit_ShouldEditExistingUser() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("John Doe");
        existingUser.setEmail("john.doe@example.com");

        User newInfo = new User();
        newInfo.setName("Jane Doe");
        newInfo.setEmail("jane.doe@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        User updatedUser = userService.edit(1L, newInfo);

        assertNotNull(updatedUser);
        assertEquals("Jane Doe", updatedUser.getName());
        assertEquals("jane.doe@example.com", updatedUser.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void edit_ShouldThrowNotFoundException_WhenUserNotFound() {
        User newInfo = new User();
        newInfo.setName("Jane Doe");
        newInfo.setEmail("jane.doe@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.edit(1L, newInfo));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getById_ShouldReturnUser() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("John Doe");
        existingUser.setEmail("john.doe@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        User foundUser = userService.getById(1L);

        assertNotNull(foundUser);
        assertEquals("John Doe", foundUser.getName());
        assertEquals("john.doe@example.com", foundUser.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getById_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void delete_ShouldDeleteUser() {
        doNothing().when(userRepository).deleteById(1L);

        userService.delete(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void validate_ShouldNotThrowException_WhenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> userService.validate(1L));
        verify(userRepository, times(1)).existsById(1L);
    }

    @Test
    void validate_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.validate(1L));
        verify(userRepository, times(1)).existsById(1L);
    }
}
