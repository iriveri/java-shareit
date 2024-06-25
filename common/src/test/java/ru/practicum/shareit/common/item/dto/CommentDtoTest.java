package ru.practicum.shareit.common.item.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ru.practicum.shareit.common.item.dto.CommentDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoTest {

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
        CommentDto dto = new CommentDto(1L, "AuthorName", "CommentText", LocalDateTime.now());

        String json = objectMapper.writeValueAsString(dto);
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"text\":\"CommentText\"");
        assertThat(json).contains("\"authorName\":\"AuthorName\"");
    }

    @Test
    void testDeserialize() throws Exception {
        String json = "{\"id\":1,\"text\":\"CommentText\",\"authorName\":\"AuthorName\",\"created\":\"2023-01-01T10:00:00\"}";

        CommentDto dto = objectMapper.readValue(json, CommentDto.class);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getText()).isEqualTo("CommentText");
        assertThat(dto.getAuthorName()).isEqualTo("AuthorName");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2023, 1, 1, 10, 0));
    }

    @Test
    void testValidCommentDto() {
        CommentDto dto = new CommentDto(1L, "CommentText", "AuthorName", LocalDateTime.now());

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void testInvalidCommentDto_EmptyText() {
        CommentDto dto = new CommentDto(1L, "AuthorName", "", LocalDateTime.now());

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("text"));
    }

}