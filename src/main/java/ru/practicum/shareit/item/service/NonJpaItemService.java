package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.ExtendedItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@Service
@Qualifier("NonJpaItemService")
public class NonJpaItemService implements ItemService {

    private final ItemStorage itemStorage;
    private final UserService userService;

    @Autowired
    public NonJpaItemService(@Qualifier("InMemoryItemStorage") ItemStorage itemStorage,@Qualifier("NonJpaUserService") UserService userService) {
        this.itemStorage = itemStorage;
        this.userService = userService;

    }

    @Override
    public Item create(Item item, Long ownerId) {
        userService.validate(ownerId);
        item.setOwnerId(ownerId);
        long id = itemStorage.addItem(item);
        return getItemById(id);
    }

    @Override
    public Item edit(Long itemId, Item item, Long ownerId) {
        itemStorage.updateItem(itemId, item, ownerId);
        return getItemById(itemId);
    }

    @Override
    public void validate(Long itemId) {
        if (!itemStorage.contains(itemId)) {
            throw new NotFoundException("Item with this ID doesnt exist");
        }
    }

    @Override
    public Item getItemById(Long itemId) {
        validate(itemId);
        return itemStorage.fetchItem(itemId);
    }

    @Override
    public ExtendedItem getAdditionalItemInfo(Item item, Long type) {
        return null;
    }

    @Override
    public Collection<Item> getItemsByOwner(Long ownerId) {
        userService.validate(ownerId);
        return itemStorage.fetchUserItems(ownerId);
    }

    @Override
    public Collection<Item> searchItemsByText(String text) {
        return itemStorage.searchForItems(text);
    }

    @Override
    public Comment addComment(Long itemId, Long userId, Comment comment) {
        return null;
    }
}
