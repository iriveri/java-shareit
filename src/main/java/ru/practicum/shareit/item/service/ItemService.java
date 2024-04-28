package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, Long userId);

    ItemDto editItem(Long itemId, ItemDto itemDto);

    ItemDto getItem(Long itemId);

    Collection<ItemDto> getAllItems();

    Collection<ItemDto> searchItemsByText(String text);
}
