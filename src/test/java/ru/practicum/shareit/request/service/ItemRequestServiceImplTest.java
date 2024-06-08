package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ExtendedItemRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemResponse;
import ru.practicum.shareit.request.storage.ItemRequestJpaRepository;
import ru.practicum.shareit.request.storage.ItemResponseJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestJpaRepository requestRepository;

    @Mock
    private ItemResponseJpaRepository responseRepository;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_ShouldSaveNewRequest() {
        User user = new User();
        user.setId(1L);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequester(user);

        when(userService.getById(1L)).thenReturn(user);
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequest savedRequest = itemRequestService.create(1L, itemRequest);

        assertNotNull(savedRequest);
        assertEquals(user, savedRequest.getRequester());
        verify(userService, times(1)).validate(1L);
        verify(requestRepository, times(1)).save(itemRequest);
    }

    @Test
    void createResponse_ShouldSaveResponse() {
        Item item = new Item();
        item.setId(1L);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);

        when(requestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(responseRepository.save(any(ItemResponse.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemResponse response = itemRequestService.createResponse(item, 1L);

        assertNotNull(response);
        assertEquals(item, response.getResponseItem());
        assertEquals(itemRequest, response.getRequest());
        verify(requestRepository, times(1)).findById(1L);
        verify(responseRepository, times(1)).save(any(ItemResponse.class));
    }

    @Test
    void createResponse_ShouldThrowNotFoundException() {
        Item item = new Item();
        item.setId(1L);

        when(requestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.createResponse(item, 1L));
        verify(requestRepository, times(1)).findById(1L);
    }

    @Test
    void getExtendedRequest_ShouldReturnExtendedRequest() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        ExtendedItemRequest result = new ExtendedItemRequest(itemRequest);
        result.setResponses(List.of());

        when(responseRepository.findAllByRequest_Id(1L)).thenReturn(Collections.emptyList());

        ExtendedItemRequest extendedRequest = itemRequestService.getExtendedRequest(itemRequest);

        assertNotNull(extendedRequest);
        assertEquals(result, extendedRequest);
        assertTrue(extendedRequest.getResponses().isEmpty());
        verify(responseRepository, times(1)).findAllByRequest_Id(1L);
    }

    @Test
    void getUserRequests_ShouldReturnUserRequests() {
        User user = new User();
        user.setId(1L);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequester(user);

        when(requestRepository.findByRequesterIdOrderByCreatedDesc(1L)).thenReturn(Collections.singletonList(itemRequest));

        List<ItemRequest> requests = itemRequestService.getUserRequests(1L);

        assertNotNull(requests);
        assertFalse(requests.isEmpty());
        assertEquals(1, requests.size());
        verify(userService, times(1)).validate(1L);
        verify(requestRepository, times(1)).findByRequesterIdOrderByCreatedDesc(1L);
    }

    @Test
    void getAllRequests_ShouldReturnRequests() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "created"));

        when(requestRepository.findByRequesterIdNot(1L, pageRequest)).thenReturn(new org.springframework.data.domain.PageImpl<>(Collections.singletonList(itemRequest)));

        List<ItemRequest> requests = itemRequestService.getAllRequests(1L, 0, 10);

        assertNotNull(requests);
        assertFalse(requests.isEmpty());
        assertEquals(1, requests.size());
        verify(userService, times(1)).validate(1L);
        verify(requestRepository, times(1)).findByRequesterIdNot(1L, pageRequest);
    }

    @Test
    void getById_ShouldReturnRequest() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);

        when(requestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));

        ItemRequest foundRequest = itemRequestService.getById(1L, 1L);

        assertNotNull(foundRequest);
        assertEquals(1L, foundRequest.getId());
        verify(userService, times(1)).validate(1L);
        verify(requestRepository, times(1)).findById(1L);
    }

    @Test
    void getById_ShouldThrowNotFoundException() {
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getById(1L, 1L));
        verify(userService, times(1)).validate(1L);
        verify(requestRepository, times(1)).findById(1L);
    }
}

