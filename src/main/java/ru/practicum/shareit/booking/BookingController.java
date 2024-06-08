package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bookings")
@Slf4j
@Validated
public class BookingController {

    private final BookingService service;
    private final BookingMapper mapper;

    @Autowired
    public BookingController(BookingService service, BookingMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    /**
     * Создание нового бронирования.
     * Endpoint: POST /bookings
     * Принимает объект BookingRequestDto в теле запроса.
     * userId в заголовке X-Sharer-User-Id — идентификатор пользователя, создающего бронирование.
     */
    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(
            @Valid @RequestBody BookingRequestDto bookingDto,
            @RequestHeader("X-Sharer-User-Id") Long bookerId
    ) {
        var booking = mapper.toBooking(bookingDto);
        booking = service.create(bookerId, booking);
        log.info("Booking for user with ID {} has been successfully created", bookerId);
        var bookingToTransfer = mapper.toResponseDto(booking);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingToTransfer);
    }

    /**
     * Обновление статуса бронирования.
     * Endpoint: PATCH /bookings/{bookingId}
     * Позволяет владельцу вещи подтвердить или отклонить бронирование.
     */
    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> updateBookingStatus(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved
    ) {
        var booking = service.updateStatus(ownerId, bookingId, approved);
        log.info("Booking status for user with ID {} has been successfully updated", ownerId);
        var bookingToTransfer = mapper.toResponseDto(booking);
        return ResponseEntity.status(HttpStatus.OK).body(bookingToTransfer);
    }

    /**
     * Получение информации о конкретном бронировании.
     * Endpoint: GET /bookings/{bookingId}
     * Информацию о бронировании может получить владелец вещи или арендатор.
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> getBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId
    ) {
        var booking = service.getByIdAndUserId(userId, bookingId);
        log.info("Booking for user with ID {} has been successfully fetched", userId);
        var bookingToTransfer = mapper.toResponseDto(booking);
        return ResponseEntity.status(HttpStatus.OK).body(bookingToTransfer);
    }

    /**
     * Получение списка бронирований пользователя.
     * Endpoint: GET /bookings
     * Возвращает список бронирований пользователя в зависимости от их статуса.
     */
    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) int offset,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(100) int limit
    ) {
        var bookings = service.getUserBookings(userId, state, offset, limit);
        log.info("Bookings for user with ID {} have been successfully fetched", userId);
        var bookingsToTransfer = bookings.stream().map(mapper::toResponseDto).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(bookingsToTransfer);
    }

    /**
     * Получение списка бронирований владельца.
     * Endpoint: GET /bookings/owner
     * Возвращает список бронирований для вещей, принадлежащих владельцу.
     */
    @GetMapping("/owner")
    public ResponseEntity<List<BookingResponseDto>> getOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) int offset,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(100) int limit
    ) {
        var bookings = service.getOwnerBookings(ownerId, state, offset, limit);
        log.info("Bookings for owner with ID {} have been successfully fetched", ownerId);
        var bookingsToTransfer = bookings.stream().map(mapper::toResponseDto).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(bookingsToTransfer);
    }
}