package ru.practicum.shareit.common.user.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@Import(UserDtoTest.Config.class)
public class UserDtoTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Validator validator;
    @TestConfiguration
    static class Config {
        @Bean
        public LocalValidatorFactoryBean validatorFactoryBean() {
            return new LocalValidatorFactoryBean();
        }
    }

    @BeforeEach
    void setUp() {
        ((LocalValidatorFactoryBean) validator).afterPropertiesSet();
    }

    @Test
    void testSerialize() throws Exception {
        UserDto dto = new UserDto(1L, "test@example.com", "John Doe");

        String json = objectMapper.writeValueAsString(dto);
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"email\":\"test@example.com\"");
        assertThat(json).contains("\"name\":\"John Doe\"");
    }

    @Test
    void testDeserialize() throws Exception {
        String json = "{\"id\":1,\"email\":\"test@example.com\",\"name\":\"John Doe\"}";

        UserDto dto = objectMapper.readValue(json, UserDto.class);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getEmail()).isEqualTo("test@example.com");
        assertThat(dto.getName()).isEqualTo("John Doe");
    }

    @Test
    void testValidUserDto() {
        UserDto dto = new UserDto(1L, "test@example.com", "John Doe");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void testInvalidUserDto_EmptyEmail() {
        UserDto dto = new UserDto(1L, "", "John Doe");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void testInvalidUserDto_InvalidEmail() {
        UserDto dto = new UserDto(1L, "invalid-email", "John Doe");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void testInvalidUserDto_EmptyName() {
        UserDto dto = new UserDto(1L, "test@example.com", "");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void testInvalidUserDto_NullName() {
        UserDto dto = new UserDto(1L, "test@example.com", null);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }
}
