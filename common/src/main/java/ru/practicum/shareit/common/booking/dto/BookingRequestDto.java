package ru.practicum.shareit.common.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.common.booking.validation.StartBeforeEnd;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
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
