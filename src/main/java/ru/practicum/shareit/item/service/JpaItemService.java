package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemJpaRepository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Qualifier("JpaItemService")
public class JpaItemService implements ItemService{

    private final ItemJpaRepository itemRepository;
    private final ItemMapper mapper;

    @Autowired
    public JpaItemService(ItemJpaRepository itemRepository, ItemMapper mapper) {
        this.itemRepository = itemRepository;
        this.mapper = mapper;
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        Item item = mapper.dtoItemToItem(itemDto);
        item.setOwnerId(ownerId);
        item = itemRepository.save(item);
        return mapper.itemToItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto edit(Long itemId, ItemDto itemDto, Long ownerId) {
        Item existingItem = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("Item not found"));
        if (!existingItem.getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("Not authorized to edit this item");
        }
        existingItem.setName(itemDto.getName());
        existingItem.setDescription(itemDto.getDescription());
        existingItem.setAvailable(itemDto.getAvailable());
        existingItem = itemRepository.save(existingItem);
        return mapper.itemToItemDto(existingItem);
    }

    @Override
    public void validate(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new IllegalArgumentException("Item not found");
        }
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("Item not found"));
        return mapper.itemToItemDto(item);
    }

    @Override
    public Collection<ItemDto> getItemsByOwner(Long ownerId) {
        Collection<Item> items = itemRepository.findByOwnerId(ownerId);
        return items.stream().map(mapper::itemToItemDto).collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> searchItemsByText(String text) {
        Collection<Item> items = itemRepository.searchForItems(text);
        return items.stream().map(mapper::itemToItemDto).collect(Collectors.toList());
    }
}
