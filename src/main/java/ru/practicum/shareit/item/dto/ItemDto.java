package ru.practicum.shareit.item.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ItemDto {
    Long id;
    Long ownerId;
    String name;
    String description;
    Boolean available;
}
