package ru.practicum.server.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.service.BookingService;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.service.ItemService;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class BookingServiceSearchTest {

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
    public void testGetUserBookingsForAllStates() {
        Booking pastBooking = createBooking(savedItem, savedUser, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2));
        Booking currentBooking = createBooking(savedItem, savedUser, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        Booking futureBooking = createBooking(savedItem, savedUser, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3));
        Booking rejectedBooking = createBooking(savedItem, savedUser, LocalDateTime.now().plusDays(6), LocalDateTime.now().plusDays(7));
        bookingService.updateStatus(savedOwner.getId(), rejectedBooking.getId(), false);
        Booking approvedBooking = createBooking(savedItem, savedUser, LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(11));
        bookingService.updateStatus(savedOwner.getId(), approvedBooking.getId(), true);

        assertBookingsState("CURRENT", 1);
        assertBookingsState("PAST", 1);
        assertBookingsState("FUTURE", 3);
        assertBookingsState("WAITING", 3);
        assertBookingsState("REJECTED", 1);
        assertBookingsState("APPROVED", 1);
        assertBookingsState("ALL", 5);

        assertBookingsStateOwner("CURRENT", 1);
        assertBookingsStateOwner("PAST", 1);
        assertBookingsStateOwner("FUTURE", 3);
        assertBookingsStateOwner("WAITING", 3);
        assertBookingsStateOwner("REJECTED", 1);
        assertBookingsStateOwner("APPROVED", 1);
        assertBookingsStateOwner("ALL", 5);
    }

    private Booking createBooking(Item item, User user, LocalDateTime start, LocalDateTime end) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(start);
        booking.setEnd(end);
        return bookingService.create(user.getId(), booking);
    }

    private void assertBookingsState(String state, int expectedSize) {
        List<Booking> bookings1 = bookingService.getUserBookings(savedUser.getId(), state, 0, 10);
        assertEquals(expectedSize, bookings1.size(), "Unexpected number of bookings for state: " + state);
        List<Booking> bookings2 = bookingService.getOwnerBookings(savedOwner.getId(), state, 0, 10);
        assertEquals(expectedSize, bookings2.size(), "Unexpected number of bookings for state: " + state);
        List<Booking> bookings3 = bookingService.getItemBookings(savedItem.getId(), state, 0, 10);
        assertEquals(expectedSize, bookings3.size(), "Unexpected number of bookings for state: " + state);
    }

    private void assertBookingsStateOwner(String state, int expectedSize) {

    }
}
