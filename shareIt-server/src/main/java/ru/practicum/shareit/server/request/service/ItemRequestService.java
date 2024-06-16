package ru.practicum.shareit.server.request.service;

import ru.practicum.shareit.server.request.model.ItemResponse;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.request.model.ExtendedItemRequest;
import ru.practicum.shareit.server.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest create(Long userId, ItemRequest requestDto);

    ItemResponse createResponse(Item item, Long requestId);

    ItemRequest getById(Long userId, Long requestId);

    ExtendedItemRequest getExtendedRequest(ItemRequest item);

    List<ItemRequest> getUserRequests(Long userId);

    List<ItemRequest> getAllRequests(Long userId, int offset, int limit);


}
