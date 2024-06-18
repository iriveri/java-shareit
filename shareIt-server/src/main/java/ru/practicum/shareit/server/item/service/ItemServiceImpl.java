package ru.practicum.shareit.server.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.booking.service.BookingService;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.model.Comment;
import ru.practicum.shareit.server.item.model.ExtendedItem;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.storage.CommentJpaRepository;
import ru.practicum.shareit.server.item.storage.ItemJpaRepository;
import ru.practicum.shareit.server.request.service.ItemRequestService;
import ru.practicum.shareit.server.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;

@Service
@Qualifier("ItemServiceImpl")
public class ItemServiceImpl implements ItemService {

    private final ItemJpaRepository itemRepository;
    private final CommentJpaRepository commentRepository;
    private final UserService userService;
    private final BookingService bookingService;
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemServiceImpl(ItemJpaRepository itemRepository, CommentJpaRepository commentRepository,
                           UserService userService, @Lazy BookingService bookingService, ItemRequestService itemRequestService) {
        this.itemRepository = itemRepository;
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.bookingService = bookingService;
        this.itemRequestService = itemRequestService;
    }

    @Override
    @Transactional
    public Item create(Item item, Long ownerId) {
        userService.validate(ownerId);
        item.setOwnerId(ownerId);
        item = itemRepository.save(item);
        if (item.getRequestId() != null) {
            itemRequestService.createResponse(item, item.getRequestId());
        }
        return item;
    }

    @Override
    @Transactional
    public Item edit(Long itemId, Item item, Long ownerId) {
        userService.validate(ownerId);
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));
        if (!existingItem.getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("Not authorized to edit this item");
        }
        updateItemFields(existingItem, item);
        return existingItem;
    }

    private void updateItemFields(Item existingItem, Item newItem) {
        if (newItem.getName() != null) {
            existingItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            existingItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            existingItem.setAvailable(newItem.getAvailable());
        }
    }

    @Override
    public void validate(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Item not found");
        }
    }

    @Override
    public Item getById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));
    }

    @Override
    public ExtendedItem getExtendedItem(Item item, Long userId) {
        ExtendedItem extendedItem = new ExtendedItem(item);

        bookingService.getLastBooking(item.getId(), userId)
                .ifPresent(extendedItem::setLastBooking);
        bookingService.getNextBooking(item.getId(), userId)
                .ifPresent(extendedItem::setNextBooking);

        extendedItem.setComments(commentRepository.findByItemId(item.getId()));

        return extendedItem;
    }

    @Override
    public Collection<Item> getItemsByOwner(Long ownerId, int offset, int limit) {
        userService.validate(ownerId);
        PageRequest pageRequest = PageRequest.of(offset / limit, limit);
        return itemRepository.findByOwnerId(ownerId, pageRequest).getContent();
    }

    @Override
    public Collection<Item> searchItemsByText(String text, int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset / limit, limit);
        return itemRepository.searchForItems(text, pageRequest).getContent();
    }

    @Override
    @Transactional
    public Comment addComment(Long itemId, Long userId, Comment comment) {
        var item = getById(itemId);
        var user = userService.getById(userId);
        if (!bookingService.isUserBookedItem(user.getId(), item.getId())) {
            throw new IllegalArgumentException("User has not rented this item");
        }

        comment.setItem(item);
        comment.setUser(user);
        comment.setCreated(LocalDateTime.now());

        return commentRepository.save(comment);
    }

}
