package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemJpaRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Qualifier("JpaItemService")
public class JpaItemService implements ItemService{

    private final ItemJpaRepository itemRepository;
    private final UserService userService;

    @Autowired
    public JpaItemService(ItemJpaRepository itemRepository,@Qualifier("JpaUserService") UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public Item create(Item item, Long ownerId) {
        userService.validate(ownerId);
        item.setOwnerId(ownerId);
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item edit(Long itemId, Item item, Long ownerId) {
        userService.validate(ownerId);
        Item existingItem = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("Item not found"));
        if (!existingItem.getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("Not authorized to edit this item");
        }
        if (item.getName() != null) {
            existingItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            existingItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            existingItem.setAvailable(item.getAvailable());
        }
        return existingItem;
    }

    @Override
    public void validate(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Item not found");
        }
    }

    @Override
    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
    }

    @Override
    public Collection<Item> getItemsByOwner(Long ownerId) {
        userService.validate(ownerId);
        return itemRepository.findByOwnerId(ownerId);
    }

    @Override
    public Collection<Item> searchItemsByText(String text) {
        return itemRepository.searchForItems(text);
    }
}
