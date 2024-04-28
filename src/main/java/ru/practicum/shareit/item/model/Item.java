package ru.practicum.shareit.item.model;

import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    Long ownerId;
    String name;
    String description;
    Boolean available;
}
