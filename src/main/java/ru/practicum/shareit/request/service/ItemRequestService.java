package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest createRequest(Long userId, ItemRequest requestDto);

    List<ItemRequest> getUserRequests(Long userId);

    List<ItemRequest> getAllRequests(Long userId, int offset, int limit);

    ItemRequest getRequestById(Long userId, Long requestId);
}
