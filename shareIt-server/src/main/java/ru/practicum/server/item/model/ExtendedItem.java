package ru.practicum.server.item.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.server.booking.model.Booking;

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
