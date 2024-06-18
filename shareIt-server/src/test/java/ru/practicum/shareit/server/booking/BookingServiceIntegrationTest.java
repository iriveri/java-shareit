package ru.practicum.shareit.server.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.common.booking.model.BookingStatus;
import ru.practicum.shareit.server.booking.service.BookingService;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.service.ItemService;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.service.UserService;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class BookingServiceIntegrationTest {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;

    private User savedUser;
    private User savedOwner;
    private Item savedItem;

    @BeforeEach
    void setUp() {

        User user = new User();
        user.setName("Jan Jack De Jui");
        user.setEmail("Jui@example.com");
        savedUser = userService.create(user);

        User owner = new User();
        owner.setName("Jan Jack De Jack");
        owner.setEmail("Jack@example.com");
        savedOwner = userService.create(owner);

        Item item = new Item();
        item.setName("Ogromniy Dar");
        item.setDescription("A big gift");
        item.setAvailable(true);
        savedItem = itemService.create(item, savedOwner.getId());
    }

    @Test
    void createBooking_ShouldCreateBooking() {
        Booking booking = new Booking();
        booking.setBooker(savedUser);
        booking.setItem(savedItem);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));

        Booking createdBooking = bookingService.create(savedUser.getId(), booking);
        assertNotNull(createdBooking);
        assertNotNull(createdBooking.getId());

        Booking retrievedBooking = entityManager.find(Booking.class, createdBooking.getId());
        assertNotNull(retrievedBooking);
        assertEquals(savedUser.getId(), retrievedBooking.getBooker().getId());
        assertEquals(savedItem.getId(), retrievedBooking.getItem().getId());
        assertEquals(booking.getStart(), retrievedBooking.getStart());
        assertEquals(booking.getEnd(), retrievedBooking.getEnd());

        Booking bookingByIdAndUserId = bookingService.getBookingById(createdBooking.getId());
        assertEquals(createdBooking, bookingByIdAndUserId);
    }

    @Test
    void updateBookingStatus_ShouldUpdateStatus() {
        Booking booking = new Booking();
        booking.setBooker(savedUser);
        booking.setItem(savedItem);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        Booking createdBooking = bookingService.create(savedUser.getId(), booking);

        Booking updatedBooking = bookingService.updateStatus(savedOwner.getId(), createdBooking.getId(), true);
        assertNotNull(updatedBooking);
        assertEquals(BookingStatus.APPROVED, updatedBooking.getStatus());

        Booking retrievedBooking = entityManager.find(Booking.class, createdBooking.getId());
        assertEquals(BookingStatus.APPROVED, retrievedBooking.getStatus());
    }

    @Test
    void getUserBookings_ShouldReturnBookings() {
        Booking booking = new Booking();
        booking.setBooker(savedUser);
        booking.setItem(savedItem);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        bookingService.create(savedUser.getId(), booking);

        List<Booking> userBookings = bookingService.getUserBookings(savedUser.getId(), "ALL", 0, 10);
        assertNotNull(userBookings);
        assertEquals(1, userBookings.size());
        assertEquals(savedUser.getId(), userBookings.get(0).getBooker().getId());
    }

    @Test
    void getOwnerBookings_ShouldReturnBookings() {
        Booking booking = new Booking();
        booking.setBooker(savedUser);
        booking.setItem(savedItem);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        bookingService.create(savedUser.getId(), booking);

        List<Booking> ownerBookings = bookingService.getOwnerBookings(savedOwner.getId(), "ALL", 0, 10);
        assertNotNull(ownerBookings);
        assertEquals(1, ownerBookings.size());
        assertEquals(savedItem.getId(), ownerBookings.get(0).getItem().getId());
    }

    @Test
    void createBooking_UnavailableItem_ShouldThrowException() {
        savedItem.setAvailable(false);
        itemService.edit(savedItem.getId(), savedItem, savedOwner.getId());

        Booking booking = new Booking();
        booking.setBooker(savedUser);
        booking.setItem(savedItem);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.create(savedUser.getId(), booking);
        });

        assertEquals("Cannot book unavailable item", exception.getMessage());
    }
}
