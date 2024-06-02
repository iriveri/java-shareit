package ru.practicum.shareit.request;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    @Override
    public ItemRequestDto createRequest(Long userId, ItemRequestDto requestDto) {
        return null;
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        return null;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, int from, int size) {
        return null;
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        return null;
    }
}
