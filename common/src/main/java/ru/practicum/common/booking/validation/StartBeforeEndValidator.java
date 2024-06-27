package ru.practicum.common.booking.validation;

import ru.practicum.common.booking.dto.BookingRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, BookingRequestDto> {

    @Override
    public boolean isValid(BookingRequestDto dto, ConstraintValidatorContext context) {
        if (dto.getStart() == null || dto.getEnd() == null) {
            return true; // @NotNull аннотация займется проверкой на null
        }
        return dto.getStart().isBefore(dto.getEnd());
    }
}