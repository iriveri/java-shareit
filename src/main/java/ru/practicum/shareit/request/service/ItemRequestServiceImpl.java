package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ExtendedItemRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemResponse;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.request.storage.ItemResponseRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;

    private final ItemResponseRepository responseRepository;
    private final UserServiceImpl userService;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository requestRepository, ItemResponseRepository responseRepository, UserServiceImpl userService) {
        this.requestRepository = requestRepository;
        this.responseRepository = responseRepository;
        this.userService = userService;
    }

    @Transactional
    @Override
    public ItemRequest createRequest(Long userId, ItemRequest itemRequest) {
        userService.validate(userId);
        itemRequest.setRequester(userService.getUserById(userId));
        itemRequest.setCreated(LocalDateTime.now());
        return requestRepository.save(itemRequest);
    }

    @Override
    public ItemResponse createResponse(Item item, Long requestId) {
        ItemResponse response = new ItemResponse();
        response.setResponseItem(item);
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));
        response.setRequest(request);
        return responseRepository.save(response);
    }

    @Override
    public ExtendedItemRequest getAdditionalItemInfo(ItemRequest itemRequest) {
        ExtendedItemRequest extendedRequest = new ExtendedItemRequest(itemRequest);
        var sad = responseRepository.findAllByRequest_Id(itemRequest.getId());
        extendedRequest.setResponses(sad);
        return extendedRequest;
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
