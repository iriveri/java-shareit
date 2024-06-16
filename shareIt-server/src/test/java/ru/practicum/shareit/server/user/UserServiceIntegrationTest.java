package ru.practicum.shareit.server.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.service.UserService;
import ru.practicum.shareit.server.user.storage.UserJpaRepository;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@SpringJUnitConfig
public class UserServiceIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserService userService;

    @Autowired
    private UserJpaRepository userRepository;

    @Test
    void createUser_ShouldCreateUser() {
        // Given
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");

        // When
        User createdUser = userService.create(user);

        // Then
        assertNotNull(createdUser.getId(), "User ID should not be null");
        User retrievedUser = entityManager.find(User.class, createdUser.getId());
        assertEquals(createdUser.getId(), retrievedUser.getId(), "Retrieved user ID should match created user ID");
        assertEquals(user.getName(), retrievedUser.getName(), "Retrieved user name should match created user name");
        assertEquals(user.getEmail(), retrievedUser.getEmail(), "Retrieved user email should match created user email");
    }

    @Test
    void editUser_ShouldUpdateUserInfo() {
        // Given
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        User createdUser = userService.create(user);

        // When
        User updatedUser = new User();
        updatedUser.setName("John Smith");
        updatedUser.setEmail("john.smith@example.com");
        User editedUser = userService.edit(createdUser.getId(), updatedUser);

        // Then
        assertEquals(updatedUser.getName(), editedUser.getName(), "Edited user name should match updated user name");
        assertEquals(updatedUser.getEmail(), editedUser.getEmail(), "Edited user email should match updated user email");
    }

    @Test
    void getById_ShouldRetrieveUserById() {
        // Given
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        User createdUser = userService.create(user);

        // When
        User retrievedUser = userService.getById(createdUser.getId());

        // Then
        assertEquals(createdUser.getId(), retrievedUser.getId(), "Retrieved user ID should match created user ID");
        assertEquals(user.getName(), retrievedUser.getName(), "Retrieved user name should match created user name");
        assertEquals(user.getEmail(), retrievedUser.getEmail(), "Retrieved user email should match created user email");
    }

    @Test
    void delete_ShouldDeleteUser() {
        // Given
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        User createdUser = userService.create(user);

        // When
        userService.delete(createdUser.getId());

        // Then
        assertFalse(userRepository.existsById(createdUser.getId()), "User should be deleted");
    }
}
