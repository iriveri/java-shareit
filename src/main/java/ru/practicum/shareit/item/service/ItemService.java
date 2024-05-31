package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.ExtendedItem;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    Item create(Item itemDto, Long ownerId);

    Item edit(Long itemId, Item itemDto, Long ownerId);

    void validate(Long itemId);

    Item getItemById(Long itemId);
    ExtendedItem getAdditionalItemInfo(Item item, Long userId);

    Collection<Item> getItemsByOwner(Long ownerId);

    Collection<Item> searchItemsByText(String text);

    Comment addComment(Long itemId, Long userId, Comment comment);
}
