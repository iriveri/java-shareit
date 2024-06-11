package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemResponseMapperTest {

    private final ItemResponseMapper mapper = Mappers.getMapper(ItemResponseMapper.class);

    @Test
    void toDto_ShouldMapCorrectly() {
        // Given
        ItemResponse itemResponse = new ItemResponse();
        itemResponse.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Description");
        item.setAvailable(true);

        itemResponse.setResponseItem(item);

        // When
        ItemResponseDto dto = mapper.toDto(itemResponse);

        // Then
        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getDescription(), dto.getDescription());
        assertEquals(item.getAvailable(), dto.getAvailable());
    }
}
