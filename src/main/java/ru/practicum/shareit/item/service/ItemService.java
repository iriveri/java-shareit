package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto create(ItemDto itemDto, Long ownerId);

    ItemDto edit(Long itemId, ItemDto itemDto, Long ownerId);

    void validate(Long itemId);

    ItemDto getItemById(Long itemId);

    Collection<ItemDto> getItemsByOwner(Long ownerId);

    Collection<ItemDto> searchItemsByText(String text);
}
