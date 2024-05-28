package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingJpaRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.service.JpaItemService;
import ru.practicum.shareit.user.service.JpaUserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingJpaRepository bookingRepository;
    private final JpaItemService itemService;
    private final JpaUserService userService;

    private final BookingMapper mapper;

    @Autowired
    public BookingServiceImpl(BookingJpaRepository bookingRepository, JpaItemService itemService, JpaUserService userService, BookingMapper mapper) {
        this.bookingRepository = bookingRepository;
        this.itemService = itemService;
        this.userService = userService;
        this.mapper = mapper;
    }
    @Transactional
    public Booking createBooking(Long userId, Booking booking) {
        userService.validate(userId);
        itemService.validate(booking.getItem().getId());
        booking.setItem(itemService.getItemById(booking.getItem().getId()));
        if(!booking.getItem().getAvailable()){
            throw new IllegalArgumentException("Cannot book unavailable item");
        }
        if(booking.getItem().getOwnerId()==userId){
            throw new NotFoundException("Cannot book unavailable item");
        }

        booking.setBooker(userService.getUserById(userId));
        booking.setStatus(BookingStatus.WAITING);
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking updateBookingStatus(Long ownerId, Long bookingId, boolean approved) {
        userService.validate(ownerId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found"));
        if (!booking.getItem().getOwnerId().equals(ownerId)) {
            throw new NotFoundException("User is not the owner of the item");
        }
        if(booking.getStatus()==BookingStatus.APPROVED||booking.getStatus()==BookingStatus.REJECTED){
            throw new IllegalArgumentException("fate already decided my friend");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return booking;
    }

    public Booking getBooking(Long userId, Long bookingId) {
        userService.validate(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found"));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwnerId().equals(userId)) {
            throw new NotFoundException("User is not authorized to view this booking");
        }
        return booking;
    }

    public List<Booking> getUserBookings(Long userId, String state) {
        userService.validate(userId);
        List<Booking> bookings;
        switch (state.toUpperCase()) {
            case "CURRENT":
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId).stream()
                        .filter(b -> b.getStart().isBefore(LocalDateTime.now()) && b.getEnd().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case "PAST":
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId).stream()
                        .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId).stream()
                        .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case "WAITING":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            case "ALL":
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
            default:
               throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");

        }
        return bookings;
    }

    public List<Booking> getOwnerBookings(Long ownerId, String state) {
        userService.validate(ownerId);
        List<Booking> bookings;
        switch (state.toUpperCase()) {
            case "CURRENT":
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId).stream()
                        .filter(b -> b.getStart().isBefore(LocalDateTime.now()) && b.getEnd().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case "PAST":
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId).stream()
                        .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case "FUTURE":
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId).stream()
                        .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case "WAITING":
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
                break;
            case "ALL":
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
                break;
            default:
                throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings;
    }
}