package ru.practicum.shareit.gateway.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.gateway.item.service.ItemServiceImpl;
import ru.practicum.shareit.gateway.user.service.UserServiceImpl;
import ru.practicum.shareit.gateway.booking.model.Booking;
import ru.practicum.shareit.gateway.booking.model.BookingStatus;
import ru.practicum.shareit.gateway.booking.storage.BookingJpaRepository;
import ru.practicum.shareit.gateway.booking.storage.BookingSpecification;
import ru.practicum.shareit.gateway.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingJpaRepository bookingRepository;
    private final ItemServiceImpl itemService;
    private final UserServiceImpl userService;

    @Autowired
    public BookingServiceImpl(BookingJpaRepository bookingRepository, ItemServiceImpl itemService,
                              @Lazy UserServiceImpl userService) {
        this.bookingRepository = bookingRepository;
        this.itemService = itemService;
        this.userService = userService;
    }

    @Transactional
    @Override
    public Booking create(Long userId, Booking booking) {
        validateUserAndItem(userId, booking);
        booking.setBooker(userService.getById(userId));
        booking.setStatus(BookingStatus.WAITING);
        return bookingRepository.save(booking);
    }

    @Transactional
    @Override
    public Booking updateStatus(Long ownerId, Long bookingId, boolean approved) {
        userService.validate(ownerId);
        Booking booking = getBookingById(bookingId);
        if (!booking.getItem().getOwnerId().equals(ownerId)) {
            throw new NotFoundException("User is not the owner of the item");
        }
        if (booking.getStatus() == BookingStatus.APPROVED || booking.getStatus() == BookingStatus.REJECTED) {
            throw new IllegalArgumentException("Booking status already decided");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return booking;
    }


    @Override
    @Transactional(readOnly = true)
    public Booking getOwnersBookingById(Long userId, Long bookingId) {
        userService.validate(userId);
        Booking booking = getBookingById(bookingId);
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwnerId().equals(userId)) {
            throw new NotFoundException("User is not authorized to view this booking");
        }
        return booking;
    }

    @Override
    public List<Booking> getUserBookings(Long userId, String state, int offset, int limit) {
        userService.validate(userId);
        PageRequest pageRequest = PageRequest.of(offset / limit, limit, Sort.by(Sort.Direction.DESC, "start"));
        Specification<Booking> byBookerId = BookingSpecification.byBookerId(userId);
        Specification<Booking> bySortingSpec = createSpecification(state);
        return bookingRepository.findAll(byBookerId.and(bySortingSpec), pageRequest).getContent();
    }

    @Override
    public List<Booking> getOwnerBookings(Long ownerId, String state, int offset, int limit) {
        userService.validate(ownerId);
        PageRequest pageRequest = PageRequest.of(offset / limit, limit, Sort.by(Sort.Direction.DESC, "start"));
        Specification<Booking> byOwnerId = BookingSpecification.byOwnerId(ownerId);
        Specification<Booking> bySortingSpec = createSpecification(state);
        return bookingRepository.findAll(byOwnerId.and(bySortingSpec), pageRequest).getContent();
    }

    @Override
    public List<Booking> getItemBookings(Long itemId, String state, int offset, int limit) {
        itemService.validate(itemId);
        PageRequest pageRequest = PageRequest.of(offset / limit, limit, Sort.by(Sort.Direction.DESC, "start"));
        Specification<Booking> byItemId = BookingSpecification.byItemId(itemId);
        Specification<Booking> bySortingSpec = createSpecification(state);
        return bookingRepository.findAll(byItemId.and(bySortingSpec), pageRequest).getContent();
    }

    @Override
    public boolean isUserBookedItem(Long userId, Long itemId) {
        return bookingRepository.haveUserBookedItem(userId, itemId, LocalDateTime.now());
    }

    @Override
    public Optional<Booking> getLastBooking(Long itemId, Long userId) {
        return bookingRepository.getLastBooking(itemId, userId, LocalDateTime.now());
    }

    @Override
    public Optional<Booking> getNextBooking(Long itemId, Long userId) {
        return bookingRepository.getNextBooking(itemId, userId, LocalDateTime.now());
    }

    private void validateUserAndItem(Long userId, Booking booking) {
        userService.validate(userId);
        itemService.validate(booking.getItem().getId());
        booking.setItem(itemService.getById(booking.getItem().getId()));
        if (!booking.getItem().getAvailable()) {
            throw new IllegalArgumentException("Cannot book unavailable item");
        }
        if (booking.getItem().getOwnerId().equals(userId)) {
            throw new NotFoundException("Cannot book own item");
        }
    }

    @Override
    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
    }


    private Specification<Booking> createSpecification(String state) {
        switch (state.toUpperCase()) {
            case "CURRENT":
                return BookingSpecification.isCurrent();
            case "PAST":
                return BookingSpecification.isPast();
            case "FUTURE":
                return BookingSpecification.isFuture();
            case "WAITING":
            case "REJECTED":
            case "APPROVED":
            case "CANCELED":
                return BookingSpecification.hasState(state);
            case "ALL":
                return (root, query, cb) -> cb.conjunction();
            default:
                throw new IllegalArgumentException("Unknown state: " + state);
        }
    }
}
