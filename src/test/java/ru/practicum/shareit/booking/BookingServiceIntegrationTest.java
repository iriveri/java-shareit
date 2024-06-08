package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.storage.BookingJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
public class BookingServiceIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingJpaRepository bookingRepository;

    @Autowired
    private UserJpaRepository userRepository;

    @Test
    void createBooking_ShouldCreateBooking() {
        // Создание пользователя
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        User savedUser = userRepository.save(user);

        // Создание бронирования
        Booking booking = new Booking();
        booking.setBooker(savedUser);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(1));

        // Вызов метода сервиса для создания бронирования
        Booking createdBooking = bookingService.create(savedUser.getId(), booking);

        // Проверка, что бронирование успешно создано
        Optional<Booking> retrievedBooking = bookingRepository.findById(createdBooking.getId());
        assertTrue(retrievedBooking.isPresent());
        assertEquals(savedUser.getId(), retrievedBooking.get().getBooker().getId());
    }
}
