package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ExtendedItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemExtensionType;

import java.util.Collection;

public interface ItemService {

    Item create(Item itemDto, Long ownerId);

    Item edit(Long itemId, Item itemDto, Long ownerId);

    void validate(Long itemId);

    Item getItemById(Long itemId);
    ExtendedItem getAdditionalItemInfo(Item item, ItemExtensionType type);

    Collection<Item> getItemsByOwner(Long ownerId);

    Collection<Item> searchItemsByText(String text);
}
