package ru.practicum.shareit.booking.service;


import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking createBooking(Long userId, Booking bookingDto);

    Booking updateBookingStatus(Long ownerId, Long bookingId, boolean approved);

    Booking getBooking(Long userId, Long bookingId);

    List<Booking> getUserBookings(Long userId, String state);

    List<Booking> getOwnerBookings(Long ownerId, String state);
}
