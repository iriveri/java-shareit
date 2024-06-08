package ru.practicum.shareit.request.service;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ExtendedItemRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemResponse;

import java.util.List;

public interface ItemRequestService {
    ItemRequest createRequest(Long userId, ItemRequest requestDto);

    ItemResponse createResponse(Item item, Long requestId);

    ExtendedItemRequest getAdditionalItemInfo(ItemRequest item);

    List<ItemRequest> getUserRequests(Long userId);

    List<ItemRequest> getAllRequests(Long userId, int offset, int limit);

    ItemRequest getRequestById(Long userId, Long requestId);

}
