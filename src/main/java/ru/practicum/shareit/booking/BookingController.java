package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

/**
 * Контроллер для управления бронированием вещей.
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    /**
     * Бронирование вещи.
     * <p>
     * Endpoint: POST /bookings
     * Принимает объект BookingDto в теле запроса.
     * userId в заголовке X-Sharer-User-Id — идентификатор пользователя, бронирующего вещь.
     * Владелец вещи должен подтвердить бронирование.
     */
    @PostMapping
    public void bookItem(@RequestBody BookingDto bookingDto, @RequestHeader("X-Sharer-User-Id") String userId) {
        // TODO: Implement method
    }

    /**
     * Подтверждение бронирования вещи.
     * <p>
     * Endpoint: PATCH /bookings/{bookingId}
     * Позволяет владельцу вещи подтвердить бронирование.
     */
    @PatchMapping("/{bookingId}")
    public void confirmBooking(@PathVariable Long bookingId) {
        // TODO: Implement method
    }

    /**
     * Отмена бронирования вещи.
     * <p>
     * Endpoint: DELETE /bookings/{bookingId}
     * Позволяет пользователю отменить бронирование вещи.
     */
    @DeleteMapping("/{bookingId}")
    public void cancelBooking(@PathVariable Long bookingId) {
        // TODO: Implement method
    }

}
