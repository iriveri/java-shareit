package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@SpringJUnitConfig
public class BookingServiceIntegrationTest {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;

    @Test
    void createBooking_ShouldCreateBooking() {
        User user = new User();
        user.setName("Jan Jack De Jui");
        user.setEmail("Jui@example.com");
        User savedUser = userService.create(user);

        User owner = new User();
        owner.setName("Jan Jack De Jack");
        owner.setEmail("Jack@example.com");
        User savedOwner = userService.create(owner);

        Item item = new Item();
        item.setName("Ogromniy Dar");
        item.setAvailable(true);
        Item savedItem = itemService.create(item, owner.getId());

        Booking booking = new Booking();
        booking.setBooker(savedUser);
        booking.setItem(savedItem);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(1));

        Booking createdBooking = bookingService.create(savedUser.getId(), booking);
        assertEquals(createdBooking, entityManager.find(Booking.class, createdBooking.getId()));

        Booking retrievedBooking = bookingService.getByIdAndUserId(createdBooking.getId(), savedUser.getId());
        assertEquals(retrievedBooking, entityManager.find(Booking.class, createdBooking.getId()));

        assertNotNull(retrievedBooking);
        assertEquals(user.getId(), retrievedBooking.getBooker().getId());
        assertEquals(item.getId(), retrievedBooking.getItem().getId());
        assertEquals(booking.getStart(), retrievedBooking.getStart());
        assertEquals(booking.getEnd(), retrievedBooking.getEnd());
    }
}
