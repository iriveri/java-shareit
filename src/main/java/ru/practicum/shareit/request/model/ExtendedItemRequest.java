package ru.practicum.shareit.request.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExtendedItemRequest extends ItemRequest {
    List<ItemResponse> responses;

    public ExtendedItemRequest(ItemRequest itemRequest) {
        super(itemRequest);
    }
}
