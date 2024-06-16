package ru.practicum.shareit.server.request.dto;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemRequestWithResponsesDtoTest {

    @Test
    void getItems() {
        List<ItemResponseDto> items = Collections.singletonList(new ItemResponseDto());
        ItemRequestWithResponsesDto dto = new ItemRequestWithResponsesDto();
        dto.setItems(items);
        assertEquals(items, dto.getItems());
    }

    @Test
    void setItems() {
        List<ItemResponseDto> items = Collections.singletonList(new ItemResponseDto());
        ItemRequestWithResponsesDto dto = new ItemRequestWithResponsesDto();
        dto.setItems(items);
        assertEquals(items, dto.getItems());
    }

    @Test
    void testToString() {
        List<ItemResponseDto> items = Collections.singletonList(new ItemResponseDto());
        ItemRequestWithResponsesDto dto = new ItemRequestWithResponsesDto();
        dto.setItems(items);
        String expected = "ItemRequestWithResponsesDto(super=ItemRequestDto(...), items=[ItemResponseDto(...)])";
        assertTrue(dto.toString().contains("ItemRequestWithResponsesDto"));
    }

    @Test
    void testEquals() {
        List<ItemResponseDto> items = Collections.singletonList(new ItemResponseDto());
        ItemRequestWithResponsesDto dto1 = new ItemRequestWithResponsesDto();
        dto1.setItems(items);

        ItemRequestWithResponsesDto dto2 = new ItemRequestWithResponsesDto();
        dto2.setItems(items);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void canEqual() {
        ItemRequestWithResponsesDto dto1 = new ItemRequestWithResponsesDto();
        ItemRequestWithResponsesDto dto2 = new ItemRequestWithResponsesDto();

        assertTrue(dto1.canEqual(dto2));
    }

    @Test
    void testHashCode() {
        List<ItemResponseDto> items = Collections.singletonList(new ItemResponseDto());
        ItemRequestWithResponsesDto dto1 = new ItemRequestWithResponsesDto();
        dto1.setItems(items);

        ItemRequestWithResponsesDto dto2 = new ItemRequestWithResponsesDto();
        dto2.setItems(items);

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}
