package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {

    Long addItem(Item item);

    void updateItem(Long itemId, Item Item, Long ownerId);

    Boolean contains(Long itemId);

    Item fetchItem(Long itemId);

    Collection<Item> fetchUserItems(Long ownerId);

    Collection<Item> searchForItems(String text);
}
