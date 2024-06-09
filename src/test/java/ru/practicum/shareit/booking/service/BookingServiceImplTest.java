package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingJpaRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    @Mock
    private BookingJpaRepository bookingRepository;

    @Mock
    private ItemServiceImpl itemService;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_ShouldSaveNewBooking() {
        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        item.setOwnerId(2L);

        Booking newBooking = new Booking();
        newBooking.setItem(item);
        newBooking.setStart(LocalDateTime.now().plusDays(1));
        newBooking.setEnd(LocalDateTime.now().plusDays(2));

        when(userService.getById(1L)).thenReturn(user);
        when(itemService.getById(1L)).thenReturn(item);
        when(bookingRepository.save(any(Booking.class))).thenReturn(newBooking);

        Booking savedBooking = bookingService.create(1L, newBooking);

        assertNotNull(savedBooking);
        assertEquals(BookingStatus.WAITING, savedBooking.getStatus());
        verify(userService, times(1)).validate(1L);
        verify(itemService, times(1)).validate(1L);
        verify(bookingRepository, times(1)).save(newBooking);
    }

    @Test
    void updateStatus_ShouldUpdateBookingStatus() {
        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setOwnerId(1L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Booking updatedBooking = bookingService.updateStatus(1L, 1L, true);

        assertNotNull(updatedBooking);
        assertEquals(BookingStatus.APPROVED, updatedBooking.getStatus());
        verify(userService, times(1)).validate(1L);
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void updateStatus_ShouldThrowNotFoundException_WhenUserNotOwner() {
        User owner = new User();
        owner.setId(2L);

        Item item = new Item();
        item.setId(1L);
        item.setOwnerId(1L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.updateStatus(2L, 1L, true));
        verify(userService, times(1)).validate(2L);
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void getByIdAndUserId_ShouldReturnBooking() {
        User booker = new User();
        booker.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setOwnerId(2L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Booking foundBooking = bookingService.getByIdAndUserId(1L, 1L);

        assertNotNull(foundBooking);
        assertEquals(1L, foundBooking.getId());
        verify(userService, times(1)).validate(1L);
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void getByIdAndUserId_ShouldThrowNotFoundException_WhenUserNotAuthorized() {
        User booker = new User();
        booker.setId(2L);

        Item item = new Item();
        item.setId(1L);
        item.setOwnerId(2L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getByIdAndUserId(3L, 1L));
        verify(userService, times(1)).validate(3L);
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void getUserBookings_ShouldReturnUserBookings() {
        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setOwnerId(2L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(user);

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "start"));

        when(userService.getById(1L)).thenReturn(user);
        when(bookingRepository.findAll(any(Specification.class), eq(pageRequest)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<Booking> bookings = bookingService.getUserBookings(1L, "ALL", 0, 10);

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        verify(userService, times(1)).validate(1L);
    }

    @Test
    void isUserBookedItem_ShouldReturnTrue() {
        when(bookingRepository.haveUserBookedItem(eq(1L), eq(1L), any(LocalDateTime.class))).thenReturn(true);

        boolean result = bookingService.isUserBookedItem(1L, 1L);

        assertTrue(result);
        verify(bookingRepository, times(1)).haveUserBookedItem(eq(1L), eq(1L), any(LocalDateTime.class));
    }

    @Test
    void getLastBooking_ShouldReturnLastBooking() {
        Booking booking = new Booking();
        booking.setId(1L);

        when(bookingRepository.getLastBooking(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(Optional.of(booking));
        Optional<Booking> lastBooking = bookingService.getLastBooking(1L, 1L);
        var asd = Optional.of(booking);
        assertTrue(lastBooking.isPresent());
        assertEquals(1L, lastBooking.get().getId());
        verify(bookingRepository, times(1)).getLastBooking(eq(1L), eq(1L), any(LocalDateTime.class));
    }

    @Test
    void getNextBooking_ShouldReturnNextBooking() {
        Booking booking = new Booking();
        booking.setId(1L);

        when(bookingRepository.getNextBooking(anyLong(), anyLong(), any(LocalDateTime.class))).thenReturn(Optional.of(booking));

        Optional<Booking> nextBooking = bookingService.getNextBooking(1L, 1L);

        assertTrue(nextBooking.isPresent());
        assertEquals(1L, nextBooking.get().getId());
        verify(bookingRepository, times(1)).getNextBooking(eq(1L), eq(1L), any(LocalDateTime.class));
    }
}
