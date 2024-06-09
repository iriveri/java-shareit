package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserJpaRepository;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");

        User createdUser = userService.create(user);

        User retrievedUser = entityManager.find(User.class, createdUser.getId());
        assertEquals(createdUser.getId(), retrievedUser.getId());
    }
}
