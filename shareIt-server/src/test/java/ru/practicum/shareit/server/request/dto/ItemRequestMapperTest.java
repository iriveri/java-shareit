package ru.practicum.shareit.server.request.dto;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.practicum.shareit.common.request.dto.ItemRequestDto;
import ru.practicum.shareit.common.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.server.booking.mapper.BookingMapper;
import ru.practicum.shareit.server.item.mapper.CommentMapper;
import ru.practicum.shareit.server.item.mapper.ItemMapper;
import ru.practicum.shareit.server.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.server.request.mapper.ItemResponseMapper;
import ru.practicum.shareit.server.request.model.ExtendedItemRequest;
import ru.practicum.shareit.server.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
class ItemRequestMapperTest {
    @Autowired
    private ItemRequestMapper itemRequestMapper;

    @TestConfiguration
    static class Config {
        @Bean
        public ItemRequestMapper itemRequestMapper() {
            return Mappers.getMapper(ItemRequestMapper.class);
        }

        @Bean
        public ItemResponseMapper itemResponseMapper() {
            return Mappers.getMapper(ItemResponseMapper.class);
        }

        @Bean
        public ItemMapper itemMapper() {
            return Mappers.getMapper(ItemMapper.class);
        }

        @Bean
        public CommentMapper commentMapper() {
            return Mappers.getMapper(CommentMapper.class);
        }

        @Bean
        public BookingMapper bookingMapper() {
            return Mappers.getMapper(BookingMapper.class);
        }

    }

    @Test
    void toItemRequest() {
        // Given
        ItemRequestDto dto = new ItemRequestDto(null, "Description", LocalDateTime.now());

        // When
        ItemRequest request = itemRequestMapper.toItemRequest(dto);

        // Then
        assertEquals(dto.getDescription(), request.getDescription());
    }

    @Test
    void toDto() {
        // Given
        ItemRequest request = new ItemRequest();
        request.setDescription("Description");
        request.setCreated(LocalDateTime.now());

        // When
        ItemRequestDto dto = itemRequestMapper.toDto(request);

        // Then
        assertEquals(request.getDescription(), dto.getDescription());
    }

    @Test
    void toWithResponsesDto() {
        // Given
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Description");
        itemRequest.setCreated(LocalDateTime.now());

        ExtendedItemRequest extendedItemRequest = new ExtendedItemRequest(itemRequest);
        extendedItemRequest.setResponses(Collections.emptyList());

        // When
        ItemRequestWithResponsesDto dto = itemRequestMapper.toWithResponsesDto(extendedItemRequest);

        // Then
        assertEquals(extendedItemRequest.getDescription(), dto.getDescription());
        assertEquals(extendedItemRequest.getResponses().size(), dto.getItems().size());
    }
}


