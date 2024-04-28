package ru.practicum.shareit.item.dto;
import org.mapstruct.Mapper;
import ru.practicum.shareit.item.model.Item;

@Mapper
public interface ItemMapper {
    ItemDto itemToItemDto(Item item);
    Item dtoItemToItem(ItemDto itemDto);
}