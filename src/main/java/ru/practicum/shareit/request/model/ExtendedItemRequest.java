package ru.practicum.shareit.request.model;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Data
public class ExtendedItemRequest extends ItemRequest {
    List<ItemResponse> responses;
    public ExtendedItemRequest(ItemRequest itemRequest) {
        super(itemRequest);
    }
}
