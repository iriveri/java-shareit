package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto addItem(ItemDto itemDto, Long ownerId);

    ItemDto editItem(Long itemId, ItemDto itemDto, Long ownerId);

    void validate(Long itemId);

    ItemDto getItem(Long itemId);

    Collection<ItemDto> getAllUserItems(Long ownerId);

    Collection<ItemDto> searchItemsByText(String text);
}
