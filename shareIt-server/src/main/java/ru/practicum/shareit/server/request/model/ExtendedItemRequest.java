package ru.practicum.shareit.server.request.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ExtendedItemRequest extends ItemRequest {
    List<ItemResponse> responses;

    public ExtendedItemRequest(ItemRequest itemRequest) {
        super(itemRequest);
    }
}
