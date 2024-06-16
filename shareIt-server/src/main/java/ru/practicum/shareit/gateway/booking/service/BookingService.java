package ru.practicum.shareit.gateway.booking.service;


import ru.practicum.shareit.gateway.booking.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    Booking create(Long userId, Booking bookingDto);

    Booking updateStatus(Long ownerId, Long bookingId, boolean approved);

    Booking getOwnersBookingById(Long userId, Long bookingId);

    List<Booking> getUserBookings(Long userId, String state, int offset, int limit);

    List<Booking> getOwnerBookings(Long ownerId, String state, int offset, int limit);

    List<Booking> getItemBookings(Long ownerId, String state, int offset, int limit);

    boolean isUserBookedItem(Long userId, Long itemId);

    Optional<Booking> getLastBooking(Long itemId, Long userId);

    Optional<Booking> getNextBooking(Long itemId, Long userId);

    Booking getBookingById(Long bookingId);
}
