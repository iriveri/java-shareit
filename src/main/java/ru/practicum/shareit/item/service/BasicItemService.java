package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
@Service
public class BasicItemService implements ItemService{
    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        return null;
    }

    @Override
    public ItemDto editItem(Long itemId, ItemDto itemDto) {
        return null;
    }

    @Override
    public ItemDto getItem(Long itemId) {
        return null;
    }

    @Override
    public Collection<ItemDto> getAllItems() {
        return null;
    }

    @Override
    public Collection<ItemDto> searchItemsByText(String text) {
        return null;
    }
}
