package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bookings")
@Slf4j
public class BookingController {

    private final BookingService service;
    private final BookingMapper mapper;

    @Autowired
    public BookingController(BookingService service, BookingMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(@Valid @RequestBody BookingRequestDto bookingDto, @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        var booking = mapper.toBooking(bookingDto);
        booking = service.createBooking(bookerId, booking);
        log.info("Booking for user with ID {} has been successfully created", bookerId);
        var bookingToTransfer = mapper.toBookingResponseDto(booking);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingToTransfer);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> updateBookingStatus(@RequestHeader("X-Sharer-User-Id") Long ownerId, @PathVariable Long bookingId, @RequestParam boolean approved) {
        var booking = service.updateBookingStatus(ownerId, bookingId, approved);
        log.info("Booking status for user with ID {} has been successfully updated", ownerId);
        var bookingToTransfer = mapper.toBookingResponseDto(booking);
        return ResponseEntity.status(HttpStatus.OK).body(bookingToTransfer);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> getBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        var booking = service.getBooking(userId, bookingId);
        log.info("Booking for user with ID {} has been successfully fetched", userId);
        var bookingToTransfer = mapper.toBookingResponseDto(booking);
        return ResponseEntity.status(HttpStatus.OK).body(bookingToTransfer);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(defaultValue = "ALL") String state) {
        var bookings = service.getUserBookings(userId, state);
        log.info("Booking for user with ID {} has been successfully fetched", userId);
        var bookingToTransfer =bookings.stream().map(mapper::toBookingResponseDto).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(bookingToTransfer);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId, @RequestParam(defaultValue = "ALL") String state) {
        var bookings = service.getOwnerBookings(ownerId, state);
        log.info("Booking for user with ID {} has been successfully fetched", ownerId);
        var bookingToTransfer =bookings.stream().map(mapper::toBookingResponseDto).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(bookingToTransfer);
    }
}