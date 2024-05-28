package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.validation.StartBeforeEnd;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@StartBeforeEnd
public class BookingRequestDto {
    @NotNull
    private Long itemId;
    @Future
    @NotNull
    private LocalDateTime start;
    @Future
    @NotNull
    private LocalDateTime end;
}
