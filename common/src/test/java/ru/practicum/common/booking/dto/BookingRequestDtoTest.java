package ru.practicum.common.booking.dto;

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
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingRequestDtoTest {

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
        BookingRequestDto dto = new BookingRequestDto(1L, LocalDateTime.of(2023, 6, 10, 10, 0), LocalDateTime.of(2023, 6, 12, 10, 0));

        String json = objectMapper.writeValueAsString(dto);
        assertThat(json).contains("\"itemId\":1");
        assertThat(json).contains("\"start\":\"2023-06-10T10:00:00\"");
        assertThat(json).contains("\"end\":\"2023-06-12T10:00:00\"");
    }

    @Test
    void testDeserialize() throws Exception {
        String json = "{\"itemId\":1,\"start\":\"2023-06-10T10:00:00\",\"end\":\"2023-06-12T10:00:00\"}";

        BookingRequestDto dto = objectMapper.readValue(json, BookingRequestDto.class);
        assertThat(dto.getItemId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2023, 6, 10, 10, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2023, 6, 12, 10, 0));
    }

    @Test
    void testValidBookingRequestDto() {
        BookingRequestDto dto = new BookingRequestDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        Set<ConstraintViolation<BookingRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void testInvalidBookingRequestDto_StartAfterEnd() {
        BookingRequestDto dto = new BookingRequestDto(1L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(1));

        Set<ConstraintViolation<BookingRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void testInvalidBookingRequestDto_NullFields() {
        BookingRequestDto dto = new BookingRequestDto(null, null, null);

        Set<ConstraintViolation<BookingRequestDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(3);
    }

    @Test
    void testInvalidBookingRequestDto_PastDates() {
        BookingRequestDto dto = new BookingRequestDto(1L, LocalDateTime.of(2022, 6, 10, 10, 0), LocalDateTime.of(2022, 6, 12, 10, 0));

        Set<ConstraintViolation<BookingRequestDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(2); // Violations for start and end dates being in the past
    }
}