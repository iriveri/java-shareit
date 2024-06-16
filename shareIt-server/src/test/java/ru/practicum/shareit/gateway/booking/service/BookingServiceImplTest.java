package ru.practicum.shareit.gateway.booking.service;

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

    private User user;
    private User owner;
    private Item item;
    private Booking newBooking;
    private Booking booking;
    private PageRequest pageRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);

        owner = new User();
        owner.setId(2L);

        item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        item.setOwnerId(2L);

        newBooking = new Booking();
        newBooking.setItem(item);
        newBooking.setStart(LocalDateTime.now().plusDays(1));
        newBooking.setEnd(LocalDateTime.now().plusDays(2));

        booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "start"));
    }

    @Test
    void create_ShouldSaveNewBooking() {
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
    void create_ShouldThrowNotFoundException_bookerIsOwner() {
        when(userService.getById(2L)).thenReturn(owner);
        when(itemService.getById(1L)).thenReturn(item);
        when(bookingRepository.save(any(Booking.class))).thenReturn(newBooking);

        assertThrows(NotFoundException.class, () -> bookingService.create(2L, newBooking));
    }

    @Test
    void create_ShouldThrowIllegalArgumentException_itemUnavailable() {
        item.setAvailable(false);

        when(userService.getById(1L)).thenReturn(owner);
        when(itemService.getById(1L)).thenReturn(item);
        when(bookingRepository.save(any(Booking.class))).thenReturn(newBooking);

        assertThrows(IllegalArgumentException.class, () -> bookingService.create(1L, newBooking));
    }

    @Test
    void updateStatus_ShouldUpdateBookingStatus() {
        item.setOwnerId(2L);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Booking updatedBooking = bookingService.updateStatus(2L, 1L, true);

        assertNotNull(updatedBooking);
        assertEquals(BookingStatus.APPROVED, updatedBooking.getStatus());
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void updateStatus_ShouldThrowNotFoundException_WhenUserNotOwner() {
        item.setOwnerId(1L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.updateStatus(2L, 1L, true));
    }

    @Test
    void updateStatus_ShouldThrowIllegalArgumentException_WhenBookingIsSet() {
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(IllegalArgumentException.class, () -> bookingService.updateStatus(2L, 1L, true));
    }

    @Test
    void getOwnersBookingById_ShouldReturnBooking() {
        booking.setBooker(user);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Booking foundBooking = bookingService.getOwnersBookingById(1L, 1L);

        assertNotNull(foundBooking);
        assertEquals(1L, foundBooking.getId());
        verify(userService, times(1)).validate(1L);
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void getByIdAndUserId_ShouldThrowNotFoundException_WhenUserNotAuthorized() {
        booking.setBooker(user);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getOwnersBookingById(3L, 1L));
        verify(userService, times(1)).validate(3L);
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void getUserBookings_ShouldReturnUserBookings() {
        booking.setBooker(user);

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
        when(bookingRepository.getLastBooking(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(Optional.of(booking));

        Optional<Booking> lastBooking = bookingService.getLastBooking(1L, 1L);

        assertTrue(lastBooking.isPresent());
        assertEquals(1L, lastBooking.get().getId());
        verify(bookingRepository, times(1)).getLastBooking(eq(1L), eq(1L), any(LocalDateTime.class));
    }

    @Test
    void getNextBooking_ShouldReturnNextBooking() {
        when(bookingRepository.getNextBooking(anyLong(), anyLong(), any(LocalDateTime.class))).thenReturn(Optional.of(booking));

        Optional<Booking> nextBooking = bookingService.getNextBooking(1L, 1L);

        assertTrue(nextBooking.isPresent());
        assertEquals(1L, nextBooking.get().getId());
        verify(bookingRepository, times(1)).getNextBooking(eq(1L), eq(1L), any(LocalDateTime.class));
    }
}
