package ru.practicum.shareit.server.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.booking.dto.BookingRequestDto;
import ru.practicum.shareit.common.booking.dto.BookingResponseDto;
import ru.practicum.shareit.server.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@Slf4j
@Validated
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(
            @Valid @RequestBody BookingRequestDto bookingDto,
            @RequestHeader("X-Sharer-User-Id") Long bookerId
    ) {
        BookingResponseDto response = bookingService.createBooking(bookingDto, bookerId);
        log.info("Booking for user with ID {} has been successfully created", bookerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> updateBookingStatus(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved
    ) {
        BookingResponseDto response = bookingService.updateBookingStatus(ownerId, bookingId, approved);
        log.info("Booking status for user with ID {} has been successfully updated", ownerId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> getBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId
    ) {
        BookingResponseDto response = bookingService.getBooking(userId, bookingId);
        log.info("Booking for user with ID {} has been successfully fetched", userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) int offset,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(100) int limit
    ) {
        List<BookingResponseDto> response = bookingService.getUserBookings(userId, state, offset, limit);
        log.info("Bookings for user with ID {} have been successfully fetched", userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingResponseDto>> getOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) int offset,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(100) int limit
    ) {
        List<BookingResponseDto> response = bookingService.getOwnerBookings(ownerId, state, offset, limit);
        log.info("Bookings for owner with ID {} have been successfully fetched", ownerId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
