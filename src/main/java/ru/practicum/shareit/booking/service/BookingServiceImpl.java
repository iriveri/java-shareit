package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingJpaRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    public Booking createBooking(Long userId, Booking booking) {
        validateUserAndItem(userId, booking);
        booking.setBooker(userService.getUserById(userId));
        booking.setStatus(BookingStatus.WAITING);
        return bookingRepository.save(booking);
    }

    @Transactional
    @Override
    public Booking updateBookingStatus(Long ownerId, Long bookingId, boolean approved) {
        userService.validate(ownerId);
        Booking booking = getBookingById(bookingId);
        validateOwnerAndBookingStatus(ownerId, booking);
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return booking;
    }

    @Transactional
    @Override
    public Booking getBooking(Long userId, Long bookingId) {
        userService.validate(userId);
        Booking booking = getBookingById(bookingId);
        validateUserAccessToBooking(userId, booking);
        return booking;
    }

    @Override
    public List<Booking> getUserBookings(Long userId, String state) {
        userService.validate(userId);
        return filterBookingsByState(bookingRepository.findByBookerIdOrderByStartDesc(userId), state);
    }

    @Override
    public List<Booking> getOwnerBookings(Long ownerId, String state) {
        userService.validate(ownerId);
        return filterBookingsByState(bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId), state);
    }

    @Override
    public List<Booking> getItemBookings(Long itemId, String state) {
        itemService.validate(itemId);
        return filterBookingsByState(bookingRepository.findByItemIdOrderByStartDesc(itemId), state);
    }

    private void validateUserAndItem(Long userId, Booking booking) {
        userService.validate(userId);
        itemService.validate(booking.getItem().getId());
        booking.setItem(itemService.getItemById(booking.getItem().getId()));
        if (!booking.getItem().getAvailable()) {
            throw new IllegalArgumentException("Cannot book unavailable item");
        }
        if (booking.getItem().getOwnerId().equals(userId)) {
            throw new NotFoundException("Cannot book own item");
        }
    }

    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
    }

    private void validateOwnerAndBookingStatus(Long ownerId, Booking booking) {
        if (!booking.getItem().getOwnerId().equals(ownerId)) {
            throw new NotFoundException("User is not the owner of the item");
        }
        if (booking.getStatus() == BookingStatus.APPROVED || booking.getStatus() == BookingStatus.REJECTED) {
            throw new IllegalArgumentException("Booking status already decided");
        }
    }

    private void validateUserAccessToBooking(Long userId, Booking booking) {
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwnerId().equals(userId)) {
            throw new NotFoundException("User is not authorized to view this booking");
        }
    }

    private List<Booking> filterBookingsByState(List<Booking> bookings, String state) {
        LocalDateTime now = LocalDateTime.now();
        switch (state.toUpperCase()) {
            case "CURRENT":
                return bookings.stream()
                        .filter(b -> b.getStart().isBefore(now) && b.getEnd().isAfter(now))
                        .collect(Collectors.toList());
            case "PAST":
                return bookings.stream()
                        .filter(b -> b.getEnd().isBefore(now))
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookings.stream()
                        .filter(b -> b.getStart().isAfter(now))
                        .collect(Collectors.toList());
            case "WAITING":
            case "REJECTED":
            case "APPROVED":
            case "CANCELED":
                BookingStatus status = BookingStatus.valueOf(state.toUpperCase());
                return bookings.stream()
                        .filter(b -> b.getStatus() == status)
                        .collect(Collectors.toList());
            case "ALL":
                return bookings;
            default:
                throw new IllegalArgumentException("Unknown state: " + state);
        }
    }
}
