package ru.practicum.server.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.service.BookingService;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.model.User;
import ru.practicum.common.booking.dto.BookingRequestDto;
import ru.practicum.common.booking.dto.BookingResponseDto;
import ru.practicum.common.booking.model.BookingStatus;
import ru.practicum.common.item.dto.ItemDto;
import ru.practicum.common.user.dto.UserDto;
import ru.practicum.server.booking.BookingController;
import ru.practicum.server.booking.mapper.BookingMapper;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private BookingMapper bookingMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private Booking booking;
    private BookingResponseDto bookingResponseDto;


    @BeforeEach
    void setUp() {
        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.WAITING);
        var user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        booking.setBooker(user);
        var item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwnerId(2L);
        booking.setItem(item);

        bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(booking.getId());
        bookingResponseDto.setBooker(new UserDto(1L, "Test User", "test@example.com"));
        bookingResponseDto.setStart(booking.getStart());
        bookingResponseDto.setEnd(booking.getEnd());
        bookingResponseDto.setStatus(booking.getStatus());
        bookingResponseDto.setItem(new ItemDto(1L, "Test Item", "Description", true, null));

        when(bookingService.create(anyLong(), any(Booking.class))).thenReturn(booking);
        when(bookingService.updateStatus(anyLong(), anyLong(), anyBoolean())).thenReturn(booking);
        when(bookingMapper.toBooking(any(BookingRequestDto.class))).thenReturn(booking);
        when(bookingMapper.toResponseDto(booking)).thenReturn(bookingResponseDto);
        when(bookingService.getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(booking));
        when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(booking));
        when(bookingService.getOwnersBookingById(anyLong(), anyLong())).thenReturn(booking);
    }

    @Test
    void createBooking_ShouldReturnCreated() throws Exception {
        BookingRequestDto bookingRequestDto = new BookingRequestDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.booker.id").value(1L))
                .andExpect(jsonPath("$.item.id").value(1L));

        verify(bookingService, times(1)).create(anyLong(), any(Booking.class));
    }



    @Test
    void updateBookingStatus_ShouldReturnOk() throws Exception {

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("WAITING"));

        verify(bookingService, times(1)).updateStatus(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void getBooking_ShouldReturnOk() throws Exception {

        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.booker.id").value(1L))
                .andExpect(jsonPath("$.item.id").value(1L));

        verify(bookingService, times(1)).getOwnersBookingById(anyLong(), anyLong());
    }

    @Test
    void getUserBookings_ShouldReturnOk() throws Exception {

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].start").exists())
                .andExpect(jsonPath("$[0].end").exists())
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[0].booker.id").value(1L))
                .andExpect(jsonPath("$[0].item.id").value(1L));

        verify(bookingService, times(1)).getUserBookings(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void getOwnerBookings_ShouldReturnOk() throws Exception {

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].start").exists())
                .andExpect(jsonPath("$[0].end").exists())
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[0].booker.id").value(1L))
                .andExpect(jsonPath("$[0].item.id").value(1L));

        verify(bookingService, times(1)).getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt());
    }
}
