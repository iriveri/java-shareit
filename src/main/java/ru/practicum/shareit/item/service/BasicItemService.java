package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class BasicItemService implements ItemService{

    private final ItemStorage itemStorage;
    private final UserService userService;
    private final ItemMapper mapper;

    @Autowired
    public BasicItemService(ItemStorage itemStorage, UserService userService, ItemMapper mapper) {
        this.itemStorage = itemStorage;
        this.userService = userService;
        this.mapper = mapper;
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, Long ownerId) {
        Item item = mapper.dtoItemToItem(itemDto);
        userService.validate(ownerId);
        item.setOwnerId(ownerId);
        long id = itemStorage.addItem(item);
        return getItem(id);
    }

    @Override
    public ItemDto editItem(Long itemId, ItemDto itemDto, Long ownerId) {
        itemStorage.updateItem(itemId, itemDto ,ownerId);
        return getItem(itemId);
    }

    @Override
    public void validate(Long itemId) {
        if (!itemStorage.contains(itemId)) {
            throw new NotFoundException("Item with this ID doesnt exist");
        }
    }

    @Override
    public ItemDto getItem(Long itemId) {
        validate(itemId);
        return mapper.itemToItemDto(itemStorage.fetchItem(itemId));
    }

    @Override
    public Collection<ItemDto> getAllUserItems(Long ownerId) {
        userService.validate(ownerId);
        return itemStorage.fetchUserItems(ownerId)
                .stream()
                .map(mapper::itemToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> searchItemsByText(String text) {
        return itemStorage.searchForItems(text)
                .stream()
                .map(mapper::itemToItemDto)
                .collect(Collectors.toList());
    }
}
