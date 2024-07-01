package ru.practicum.server.user.dto;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.practicum.common.user.dto.UserDto;
import ru.practicum.server.user.mapper.UserMapper;
import ru.practicum.server.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;


@JsonTest
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;


    @TestConfiguration
    static class Config {
        @Bean
        public UserMapper userMapper() {
            return Mappers.getMapper(UserMapper.class);
        }
    }

    @Test
    void testToDto() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        // When
        UserDto userDto = userMapper.toDto(user);

        // Then
        assertEquals(user.getId(), userDto.getId(), "User ID should match");
        assertEquals(user.getName(), userDto.getName(), "User name should match");
        assertEquals(user.getEmail(), userDto.getEmail(), "User email should match");
    }

    @Test
    void testToUser() {
        // Given
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("John Doe");
        userDto.setEmail("john@example.com");

        // When
        User user = userMapper.toUser(userDto);

        // Then
        assertEquals(userDto.getId(), user.getId(), "User ID should match");
        assertEquals(userDto.getName(), user.getName(), "User name should match");
        assertEquals(userDto.getEmail(), user.getEmail(), "User email should match");
    }
}

