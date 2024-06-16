package ru.practicum.shareit.server.item.service;

import ru.practicum.shareit.server.item.model.Comment;
import ru.practicum.shareit.server.item.model.ExtendedItem;
import ru.practicum.shareit.server.item.model.Item;

import java.util.Collection;

public interface ItemService {

    Item create(Item itemDto, Long ownerId);

    Item edit(Long itemId, Item itemDto, Long ownerId);

    void validate(Long itemId);

    Item getById(Long itemId);

    ExtendedItem getExtendedItem(Item item, Long userId);

    Collection<Item> getItemsByOwner(Long ownerId, int offset, int limit);

    Collection<Item> searchItemsByText(String text, int offset, int limit);

    Comment addComment(Long itemId, Long userId, Comment comment);
}
