package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserServiceImpl userService;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository requestRepository, UserServiceImpl userService) {
        this.requestRepository = requestRepository;
        this.userService = userService;
    }

    @Transactional
    @Override
    public ItemRequest createRequest(Long userId, ItemRequest itemRequest) {
        userService.validate(userId);
        itemRequest.setRequester(userService.getUserById(userId));
        return requestRepository.save(itemRequest);
    }

    @Override
    public List<ItemRequest> getUserRequests(Long userId) {
        userService.validate(userId);
        return requestRepository.findByRequesterIdOrderByCreatedDesc(userId);
    }

    @Override
    public List<ItemRequest> getAllRequests(Long userId, int offset, int limit) {
        userService.validate(userId);
        PageRequest pageRequest = PageRequest.of(offset / limit, limit, Sort.by(Sort.Direction.DESC, "created"));
        return requestRepository.findByRequesterIdNot(userId, pageRequest).getContent();
    }

    @Override
    public ItemRequest getRequestById(Long userId, Long requestId) {
        userService.validate(userId);
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));
    }
}
