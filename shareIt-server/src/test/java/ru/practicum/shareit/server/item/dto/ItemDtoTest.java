package ru.practicum.shareit.server.item.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest

public class ItemDtoTest {

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
        ItemDto dto = new ItemDto(1L, "ItemName", "ItemDescription", true, 2L);

        String json = objectMapper.writeValueAsString(dto);
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"ItemName\"");
        assertThat(json).contains("\"description\":\"ItemDescription\"");
        assertThat(json).contains("\"available\":true");
        assertThat(json).contains("\"requestId\":2");
    }

    @Test
    void testDeserialize() throws Exception {
        String json = "{\"id\":1,\"name\":\"ItemName\",\"description\":\"ItemDescription\",\"available\":true,\"requestId\":2}";

        ItemDto dto = objectMapper.readValue(json, ItemDto.class);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("ItemName");
        assertThat(dto.getDescription()).isEqualTo("ItemDescription");
        assertThat(dto.getAvailable()).isEqualTo(true);
        assertThat(dto.getRequestId()).isEqualTo(2L);
    }

    @Test
    void testValidItemDto() {
        ItemDto dto = new ItemDto(1L, "ItemName", "ItemDescription", true, 2L);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void testInvalidItemDto_EmptyName() {
        ItemDto dto = new ItemDto(1L, "", "ItemDescription", true, 2L);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void testInvalidItemDto_NullAvailable() {
        ItemDto dto = new ItemDto(1L, "ItemName", "ItemDescription", null, 2L);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("available"));
    }
}
