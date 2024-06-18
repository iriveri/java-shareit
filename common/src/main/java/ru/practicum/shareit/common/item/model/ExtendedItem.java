package ru.practicum.shareit.common.item.model;

import ru.practicum.shareit.common.booking.model.Booking;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExtendedItem extends Item {
    private Booking lastBooking;
    private Booking nextBooking;
    private Collection<Comment> comments;

    public ExtendedItem(Item item) {
        super(item);
    }
}
