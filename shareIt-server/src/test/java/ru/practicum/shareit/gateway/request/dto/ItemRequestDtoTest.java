package ru.practicum.shareit.gateway.request.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@Import(ItemRequestDtoTest.Config.class)
public class ItemRequestDtoTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Validator validator;

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
        ItemRequestDto dto = new ItemRequestDto(1L, "Need a drill", LocalDateTime.of(2024, 6, 9, 10, 0));

        String json = objectMapper.writeValueAsString(dto);
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"description\":\"Need a drill\"");
        assertThat(json).contains("\"created\":\"2024-06-09T10:00:00\"");
    }

    @Test
    void testDeserialize() throws Exception {
        String json = "{\"id\":1,\"description\":\"Need a drill\",\"created\":\"2024-06-09T10:00:00\"}";

        ItemRequestDto dto = objectMapper.readValue(json, ItemRequestDto.class);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Need a drill");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2024, 6, 9, 10, 0));
    }

    @Test
    void testValidItemRequestDto() {
        ItemRequestDto dto = new ItemRequestDto(1L, "Need a drill", LocalDateTime.of(2024, 6, 9, 10, 0));

        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void testInvalidItemRequestDto_EmptyDescription() {
        ItemRequestDto dto = new ItemRequestDto(1L, "", LocalDateTime.of(2024, 6, 9, 10, 0));

        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("description"));
    }

    @Test
    void testInvalidItemRequestDto_NullDescription() {
        ItemRequestDto dto = new ItemRequestDto(1L, null, LocalDateTime.of(2024, 6, 9, 10, 0));

        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("description"));
    }
}
