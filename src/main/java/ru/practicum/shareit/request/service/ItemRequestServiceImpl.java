package ru.practicum.shareit.request.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    @Override
    public ItemRequest createRequest(Long userId, ItemRequest requestDto) {
        return null;
    }

    @Override
    public List<ItemRequest> getUserRequests(Long userId) {
        return null;
    }

    @Override
    public List<ItemRequest> getAllRequests(Long userId, int from, int size) {
        return null;
    }

    @Override
    public ItemRequest getRequestById(Long userId, Long requestId) {
        return null;
    }
}
