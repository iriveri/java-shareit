package ru.practicum.gateway.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.common.request.dto.ItemRequestDto;
import ru.practicum.common.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.gateway.request.service.ItemRequestService;


import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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
