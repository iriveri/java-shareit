
package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;


@Repository
public class DatabaseItemStorage implements ItemStorage {

    @Override
    public Long addItem(Item item) {
        return null;
    }

    @Override
    public void updateItem(Long itemId, ItemDto itemDto, Long ownerId) {

    }

    @Override
    public Boolean contains(Long itemId) {
        return null;
    }

    @Override
    public Item fetchItem(Long itemId) {
        return null;
    }

    @Override
    public Collection<Item> fetchUserItems(Long ownerId) {
        return null;
    }

    @Override
    public Collection<Item> searchForItems(String text) {
        return null;
    }
}