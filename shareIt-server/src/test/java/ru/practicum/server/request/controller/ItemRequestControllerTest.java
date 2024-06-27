package ru.practicum.server.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.server.request.ItemRequestController;
import ru.practicum.server.request.mapper.ItemRequestMapper;
import ru.practicum.server.request.model.ExtendedItemRequest;
import ru.practicum.server.request.service.ItemRequestService;
import ru.practicum.server.user.model.User;
import ru.practicum.common.request.dto.ItemRequestDto;
import ru.practicum.common.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.server.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService requestService;

    @MockBean
    private ItemRequestMapper requestMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemRequestDto requestDto;
    private ItemRequest request;
    private ItemRequestDto responseDto;
    private ItemRequestWithResponsesDto withResponsesDto;
    private ExtendedItemRequest extendedItemRequest;
    private List<ItemRequest> userRequests;
    private List<ItemRequest> allRequests;

    @BeforeEach
    void setUp() {
        // Initialize objects with predefined data
        requestDto = new ItemRequestDto(null, "Request description", LocalDateTime.now());
        request = new ItemRequest();
        request.setRequester(new User());
        responseDto = new ItemRequestDto(1L, "Request description", LocalDateTime.now());
        withResponsesDto = new ItemRequestWithResponsesDto();
        extendedItemRequest = new ExtendedItemRequest(request);
        userRequests = Collections.singletonList(request);
        allRequests = Collections.singletonList(request);

        // Mock interactions
        Mockito.when(requestMapper.toItemRequest(requestDto)).thenReturn(request);
        Mockito.when(requestService.create(anyLong(), any(ItemRequest.class))).thenReturn(request);
        Mockito.when(requestMapper.toDto(request)).thenReturn(responseDto);
        Mockito.when(requestService.getUserRequests(anyLong())).thenReturn(userRequests);
        Mockito.when(requestService.getExtendedRequest(any(ItemRequest.class))).thenReturn(extendedItemRequest);
        Mockito.when(requestMapper.toWithResponsesDto(any(ExtendedItemRequest.class))).thenReturn(withResponsesDto);
        Mockito.when(requestService.getAllRequests(anyLong(), anyInt(), anyInt())).thenReturn(allRequests);
    }

    @Test
    void createRequest_ShouldReturnCreated() throws Exception {
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Request description"));
    }

    @Test
    void getUserRequests_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getAllRequests_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }
}
