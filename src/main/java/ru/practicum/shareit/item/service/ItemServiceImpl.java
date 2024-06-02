package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.ExtendedItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentJpaRepository;
import ru.practicum.shareit.item.storage.ItemJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Qualifier("ItemServiceImpl")
public class ItemServiceImpl implements ItemService {

    private final ItemJpaRepository itemRepository;
    private final CommentJpaRepository commentRepository;
    private final UserService userService;
    private final BookingService bookingService;

    @Autowired
    public ItemServiceImpl(ItemJpaRepository itemRepository, CommentJpaRepository commentRepository,
                           UserService userService, @Lazy BookingService bookingService) {
        this.itemRepository = itemRepository;
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.bookingService = bookingService;
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
    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));
    }

    @Override
    @Transactional
    public ExtendedItem getAdditionalItemInfo(Item item, Long userId) {
        ExtendedItem extendedItem = new ExtendedItem(item);
        var bookings = bookingService.getItemBookings(item.getId(), "APPROVED")
                .stream()
                .filter(booking -> booking.getItem().getOwnerId().equals(userId))
                .collect(Collectors.toList());

        Optional<Booking> lastBooking = bookings.stream()
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getStart));

        Optional<Booking> nextBooking = bookings.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getStart));

        lastBooking.ifPresent(extendedItem::setLastBooking);
        nextBooking.ifPresent(extendedItem::setNextBooking);
        extendedItem.setComments(commentRepository.findByItemId(item.getId()));

        return extendedItem;
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

    @Override
    @Transactional
    public Comment addComment(Long itemId, Long userId, Comment comment) {
        var item = getItemById(itemId);
        var user = userService.getUserById(userId);
        validateUserHasRentedItem(item, user);

        comment.setItem(item);
        comment.setUser(user);
        comment.setCreated(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    private void validateUserHasRentedItem(Item item, User user) {
        var bookingList = bookingService.getUserBookings(user.getId(), "APPROVED")
                .stream()
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()) && booking.getItem().getId().equals(item.getId()))
                .collect(Collectors.toList());

        if (bookingList.isEmpty()) {
            throw new IllegalArgumentException("User has not rented this item");
        }
    }
}
