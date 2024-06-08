package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
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

    @Test
    void createBooking_ShouldReturnCreated() throws Exception {
        BookingRequestDto bookingRequestDto = new BookingRequestDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        Booking booking = new Booking();

        Mockito.when(bookingMapper.toBooking(bookingRequestDto)).thenReturn(booking);
        Mockito.when(bookingMapper.toResponseDto(booking)).thenReturn(bookingResponseDto);
        Mockito.when(bookingService.create(anyLong(), any(Booking.class))).thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createBooking_InvalidDates_ShouldReturnBadRequest() throws Exception {
        BookingRequestDto bookingRequestDto = new BookingRequestDto(1L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBookingStatus_ShouldReturnOk() throws Exception {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        Booking booking = new Booking();

        Mockito.when(bookingService.updateStatus(anyLong(), anyLong(), anyBoolean())).thenReturn(booking);
        Mockito.when(bookingMapper.toResponseDto(booking)).thenReturn(bookingResponseDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getBooking_ShouldReturnOk() throws Exception {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        Booking booking = new Booking();

        Mockito.when(bookingService.getByIdAndUserId(anyLong(), anyLong())).thenReturn(booking);
        Mockito.when(bookingMapper.toResponseDto(booking)).thenReturn(bookingResponseDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getUserBookings_ShouldReturnOk() throws Exception {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        Booking booking = new Booking();

        Mockito.when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(booking));
        Mockito.when(bookingMapper.toResponseDto(booking)).thenReturn(bookingResponseDto);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getOwnerBookings_ShouldReturnOk() throws Exception {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        Booking booking = new Booking();

        Mockito.when(bookingService.getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(booking));
        Mockito.when(bookingMapper.toResponseDto(booking)).thenReturn(bookingResponseDto);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }
}
