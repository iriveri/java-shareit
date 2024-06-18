package ru.practicum.shareit.common.item.dto;

import ru.practicum.shareit.common.booking.dto.BookingShortDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


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
