package ru.practicum.shareit.server.booking.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.common.booking.model.BookingStatus;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.model.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookingJpaRepositoryTest {

    @Autowired
    private BookingJpaRepository bookingJpaRepository;
    @Autowired
    private EntityManager entityManager;

    private User user;
    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Jan Jack De Jui");
        user.setEmail("Jui@example.com");
        entityManager.persist(user);

        owner = new User();
        owner.setName("Jan Jack De Jack");
        owner.setEmail("Jack@example.com");
        entityManager.persist(owner);
        entityManager.flush();

        item = new Item();
        item.setOwnerId(owner.getId());
        item.setName("Ogromniy Dar");
        item.setAvailable(true);
        entityManager.persist(item);
        entityManager.flush();

        // Добавление тестовых данных
        Booking pastBooking = new Booking();
        pastBooking.setStatus(BookingStatus.APPROVED);
        pastBooking.setItem(item);
        pastBooking.setBooker(user);
        pastBooking.setStart(LocalDateTime.now().minusDays(5));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        entityManager.persist(pastBooking);

        Booking futureBooking = new Booking();
        futureBooking.setStatus(BookingStatus.APPROVED);
        futureBooking.setItem(item);
        futureBooking.setBooker(user);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(5));
        entityManager.persist(futureBooking);
        entityManager.flush();
    }

    @Test
    @DisplayName("Test isUserBookedItem")
    void isUserBookedItem_ShouldReturnTrue() {
        Long bookerId = user.getId();
        Long itemId = item.getId();
        LocalDateTime currentTime = LocalDateTime.now();

        boolean result = bookingJpaRepository.haveUserBookedItem(bookerId, itemId, currentTime);

        assertThat(result).isTrue();  // Проверка наличия бронирования
    }

    @Test
    @DisplayName("Test getLastBooking")
    void getLastBooking_ShouldReturnBooking() {
        Long itemId = item.getId();
        Long userId = owner.getId();
        LocalDateTime now = LocalDateTime.now();

        Optional<Booking> booking = bookingJpaRepository.getLastBooking(itemId, userId, now);

        assertThat(booking).isPresent();
    }

    @Test
    @DisplayName("Test getNextBooking")
    void getNextBooking_ShouldReturnBooking() {
        Long itemId = item.getId();
        Long userId = owner.getId();
        LocalDateTime now = LocalDateTime.now();

        Optional<Booking> booking = bookingJpaRepository.getNextBooking(itemId, userId, now);

        assertThat(booking).isPresent();
    }

    @Test
    @DisplayName("Test findOverlappingBookings")
    void findOverlappingBookings_ShouldReturnBookings() {
        Long itemId = item.getId();
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        List<Booking> bookings = bookingJpaRepository.findOverlappingBookings(itemId, start, end);

        assertThat(bookings).isNotEmpty();
    }

    @Test
    @DisplayName("Test hasState with APPROVED status")
    void hasState_WithApprovedStatus_ShouldReturnMatchingBookings() {
        Specification<Booking> spec = BookingSpecification.hasState("APPROVED");

        List<Booking> bookings = bookingJpaRepository.findAll(spec);

        assertThat(bookings).hasSize(2); // Ожидаем два бронирования с статусом APPROVED
    }

    @Test
    @DisplayName("Test isCurrent")
    void isCurrent_ShouldReturnCurrentBookings() {
        Specification<Booking> spec = BookingSpecification.isCurrent();

        List<Booking> bookings = bookingJpaRepository.findAll(spec);

        assertThat(bookings).isEmpty(); // Нет текущих бронирований, так как тестовые данные не содержат текущих бронирований
    }

    @Test
    @DisplayName("Test isPast")
    void isPast_ShouldReturnPastBookings() {
        Specification<Booking> spec = BookingSpecification.isPast();

        List<Booking> bookings = bookingJpaRepository.findAll(spec);

        assertThat(bookings).hasSize(1); // Ожидаем одно прошлое бронирование
    }

    @Test
    @DisplayName("Test isFuture")
    void isFuture_ShouldReturnFutureBookings() {
        Specification<Booking> spec = BookingSpecification.isFuture();

        List<Booking> bookings = bookingJpaRepository.findAll(spec);

        assertThat(bookings).hasSize(1); // Ожидаем одно будущее бронирование
    }

    @Test
    @DisplayName("Test byOwnerId")
    void byOwnerId_ShouldReturnBookingsByOwner() {
        Specification<Booking> spec = BookingSpecification.byOwnerId(owner.getId());

        List<Booking> bookings = bookingJpaRepository.findAll(spec);

        assertThat(bookings).hasSize(2); // Ожидаем два бронирования по владельцу
    }

    @Test
    @DisplayName("Test byBookerId")
    void byBookerId_ShouldReturnBookingsByBooker() {
        Specification<Booking> spec = BookingSpecification.byBookerId(user.getId());

        List<Booking> bookings = bookingJpaRepository.findAll(spec);

        assertThat(bookings).hasSize(2); // Ожидаем два бронирования по бронирующему
    }

    @Test
    @DisplayName("Test byItemId")
    void byItemId_ShouldReturnBookingsByItem() {
        Specification<Booking> spec = BookingSpecification.byItemId(item.getId());

        List<Booking> bookings = bookingJpaRepository.findAll(spec);

        assertThat(bookings).hasSize(2); // Ожидаем два бронирования по предмету
    }
}
