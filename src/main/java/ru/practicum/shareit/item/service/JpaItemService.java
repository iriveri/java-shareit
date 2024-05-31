package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.ExtendedItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemExtensionType;
import ru.practicum.shareit.item.storage.CommentJpaRepository;
import ru.practicum.shareit.item.storage.ItemJpaRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Qualifier("JpaItemService")
public class JpaItemService implements ItemService {

    private final ItemJpaRepository itemRepository;
    private final CommentJpaRepository commentRepository;
    private final UserService userService;
    private final BookingService bookingService;

    @Autowired
    public JpaItemService(ItemJpaRepository itemRepository, CommentJpaRepository commentRepository, @Qualifier("JpaUserService") UserService userService, BookingService bookingService) {
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

    public ExtendedItem getAdditionalItemInfo(Item item, ItemExtensionType type) {
        var extendedItem = new ExtendedItem(item);
        switch (type) {
            case BOOKING_TIME:
                var bookings = bookingService.getItemBookings(item.getId(), "APPROVED");

                // Находим последнее исполненное/исполняемое бронирование
                Optional<Booking> lastBooking = bookings.stream()
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                        .max(Comparator.comparing(Booking::getStart));

                // Находим следующее не исполненное бронирование
                Optional<Booking> nextBooking = bookings.stream()
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .min(Comparator.comparing(Booking::getStart));

                lastBooking.ifPresent(extendedItem::setLastBooking);
                nextBooking.ifPresent(extendedItem::setNextBooking);

                break;
            case COMMENTS:
                break;
            case BOOKING_TIME_AND_COMMENTS:

                break;
            default:
                throw new RuntimeException("This ItemExtensionType not yet implemented");
        }
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
    public Comment addComment(Long itemId, Long userId, Comment comment) {
        var item = getItemById(itemId);
        var user = userService.getUserById(userId);
        var bookingList = bookingService.getUserBookings(user.getId(),"APPROVED").stream()
                .filter(booking->booking.getEnd().isBefore( LocalDateTime.now())&&booking.getItem().getId()==item.getId())
                .collect(Collectors.toList());

        if (bookingList.isEmpty()) {
            throw new NotFoundException("User has not rented this item");
        }

        comment.setItem(item);
        comment.setUser(user);
        comment.setCreated(LocalDateTime.now());

        return commentRepository.save(comment);
    }
}
