package ru.practicum.shareit.server.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.common.request.dto.ItemRequestDto;
import ru.practicum.shareit.common.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.server.request.service.ItemRequestService;


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


    @Autowired
    private ObjectMapper objectMapper;

    private ItemRequestDto requestDto;
    private ItemRequestDto responseDto;
    private ItemRequestWithResponsesDto withResponsesDto;

    @BeforeEach
    void setUp() {
        // Initialize objects with predefined data
        requestDto = new ItemRequestDto(null, "Request description", LocalDateTime.now());
        responseDto = new ItemRequestDto(1L, "Request description", LocalDateTime.now());
        withResponsesDto = new ItemRequestWithResponsesDto();



    }

}
