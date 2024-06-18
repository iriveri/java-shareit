package ru.practicum.shareit.common.booking.dto;

import ru.practicum.shareit.common.booking.model.BookingStatus;
import ru.practicum.shareit.common.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.common.item.dto.ItemDto;


import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class BookingResponseDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private UserDto booker;
    private ItemDto item;
}
