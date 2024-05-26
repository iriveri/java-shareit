package ru.practicum.shareit.item.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Qualifier("InMemoryItemStorage")
public class InMemoryItemStorage implements ItemStorage {
    private static long genId = 0;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Long addItem(Item item) {
        if (items.values().stream().anyMatch(existingItem -> existingItem.getName().equals(item.getName()))) {
            throw new DuplicateException("Item with name " + item.getName() + " already exists.");
        }
        long id = ++genId;
        item.setId(id);
        items.put(id, item);
        return id;
    }

    @Override
    public void updateItem(Long itemId, ItemDto itemDto, Long ownerId) {
        if (!contains(itemId)) {
            throw new IllegalArgumentException("Item with Id: " + itemId + " not found in the list!");
        }
        Item item = items.get(itemId);
        if (!Objects.equals(ownerId, item.getOwnerId())) {
            throw new NotFoundException(" ownerId: " + ownerId + " is illegal for this item");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        items.put(itemId, item);
    }

    @Override
    public Boolean contains(Long itemId) {
        return items.containsKey(itemId);
    }

    @Override
    public Item fetchItem(Long itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Item with Id " + itemId + " not found");
        }
        return item;
    }

    @Override
    public Collection<Item> fetchUserItems(Long ownerId) {
        return items.values().stream()
                .filter(item -> Objects.equals(item.getOwnerId(), ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> searchForItems(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        String refactoredText = text.toLowerCase();
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(refactoredText) || item.getDescription().toLowerCase().contains(refactoredText))
                .collect(Collectors.toList());
    }
}
