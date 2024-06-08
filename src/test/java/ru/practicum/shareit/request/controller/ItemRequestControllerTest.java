package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.request.model.ExtendedItemRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.Collections;

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

    @Test
    void createRequest_ShouldReturnCreated() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto(null, "Request description", LocalDateTime.now());
        ItemRequest request = new ItemRequest();
        ItemRequestDto responseDto = new ItemRequestDto(1L, "Request description", LocalDateTime.now());

        Mockito.when(requestMapper.toItemRequest(requestDto)).thenReturn(request);
        Mockito.when(requestService.create(anyLong(), any(ItemRequest.class))).thenReturn(request);
        Mockito.when(requestMapper.toDto(request)).thenReturn(responseDto);

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
        ItemRequest request = new ItemRequest();
        ExtendedItemRequest extendedItemRequest = new ExtendedItemRequest();
        ItemRequestWithResponsesDto responseDto = new ItemRequestWithResponsesDto();

        Mockito.when(requestService.getUserRequests(anyLong())).thenReturn(Collections.singletonList(request));
        Mockito.when(requestService.getExtendedRequest(any(ItemRequest.class))).thenReturn(extendedItemRequest);
        Mockito.when(requestMapper.toWithResponsesDto(any(ExtendedItemRequest.class))).thenReturn(responseDto);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getAllRequests_ShouldReturnOk() throws Exception {
        ItemRequest request = new ItemRequest();
        ExtendedItemRequest extendedItemRequest = new ExtendedItemRequest();
        ItemRequestWithResponsesDto responseDto = new ItemRequestWithResponsesDto();

        Mockito.when(requestService.getAllRequests(anyLong(), anyInt(), anyInt())).thenReturn(Collections.singletonList(request));
        Mockito.when(requestService.getExtendedRequest(any(ItemRequest.class))).thenReturn(extendedItemRequest);
        Mockito.when(requestMapper.toWithResponsesDto(any(ExtendedItemRequest.class))).thenReturn(responseDto);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getRequestById_ShouldReturnOk() throws Exception {
        ItemRequest request = new ItemRequest();
        ExtendedItemRequest extendedItemRequest = new ExtendedItemRequest();
        ItemRequestWithResponsesDto responseDto = new ItemRequestWithResponsesDto();

        Mockito.when(requestService.getById(anyLong(), anyLong())).thenReturn(request);
        Mockito.when(requestService.getExtendedRequest(any(ItemRequest.class))).thenReturn(extendedItemRequest);
        Mockito.when(requestMapper.toWithResponsesDto(any(ExtendedItemRequest.class))).thenReturn(responseDto);

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
