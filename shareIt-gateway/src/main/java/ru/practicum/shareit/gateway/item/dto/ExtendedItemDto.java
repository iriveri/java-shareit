package ru.practicum.shareit.gateway.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.gateway.booking.dto.BookingShortDto;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode(callSuper = true)
public class ExtendedItemDto extends ItemDto {
    BookingShortDto lastBooking;
    BookingShortDto nextBooking;
    Collection<CommentDto> comments;
}
