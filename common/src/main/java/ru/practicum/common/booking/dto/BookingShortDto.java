package ru.practicum.common.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class BookingShortDto {
    private Long id;
    private Long bookerId;
}
