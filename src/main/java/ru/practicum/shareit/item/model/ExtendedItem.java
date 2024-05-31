package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;

@Data
public class ExtendedItem extends Item {
    Booking lastBooking;
    Booking nextBooking;
    public ExtendedItem(Item item) {
        super(item);
    }
}
