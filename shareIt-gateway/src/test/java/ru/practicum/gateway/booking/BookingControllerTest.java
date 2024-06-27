package ru.practicum.gateway.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.gateway.booking.service.BookingService;
import ru.practicum.common.booking.dto.BookingRequestDto;
import ru.practicum.common.booking.dto.BookingResponseDto;
import ru.practicum.common.booking.model.BookingStatus;
import ru.practicum.common.item.dto.ItemDto;
import ru.practicum.common.user.dto.UserDto;

import java.time.LocalDateTime;

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

    @Autowired
    private ObjectMapper objectMapper;

    private BookingResponseDto bookingResponseDto;


    @BeforeEach
    void setUp() {

        bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(1L);
        bookingResponseDto.setBooker(new UserDto(1L, "Test User", "test@example.com"));
        bookingResponseDto.setStart(LocalDateTime.now().plusDays(1));
        bookingResponseDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingResponseDto.setStatus(BookingStatus.APPROVED);
        bookingResponseDto.setItem(new ItemDto(1L, "Test Item", "Description", true, null));

    }


    @Test
    void createBooking_InvalidDates_ShouldReturnBadRequest() throws Exception {
        BookingRequestDto bookingRequestDto = new BookingRequestDto(1L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest());

        verify(bookingService, times(0)).createBooking(any(BookingRequestDto.class), anyLong());
    }

    @Test
    void createBooking_NoId_ShouldReturnBadRequest() throws Exception {
        BookingRequestDto bookingRequestDto = new BookingRequestDto(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest());

        verify(bookingService, times(0)).createBooking(any(BookingRequestDto.class), anyLong());
    }

    @Test
    void createBooking_NoStart_ShouldReturnBadRequest() throws Exception {
        BookingRequestDto bookingRequestDto = new BookingRequestDto(1L, null, LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest());

        verify(bookingService, times(0)).createBooking(any(BookingRequestDto.class), anyLong());
    }

    @Test
    void createBooking_NoEnd_ShouldReturnBadRequest() throws Exception {
        BookingRequestDto bookingRequestDto = new BookingRequestDto(1L, LocalDateTime.now().plusDays(2), null);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest());

        verify(bookingService, times(0)).createBooking(any(BookingRequestDto.class), anyLong());
    }

    @Test
    void getUserBookings_fromNegative_ShouldReturnBadRequest() throws Exception {
        BookingRequestDto bookingRequestDto = new BookingRequestDto(1L, LocalDateTime.now().plusDays(2), null);

        mockMvc.perform(get("/bookings")

                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verify(bookingService, times(0)).getUserBookings(anyLong(), any(), anyInt(), anyInt());
    }

    @Test
    void getUserBookings_sizeOver100_ShouldReturnBadRequest() throws Exception {
        BookingRequestDto bookingRequestDto = new BookingRequestDto(1L, LocalDateTime.now().plusDays(2), null);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "101"))
                .andExpect(status().isBadRequest());

        verify(bookingService, times(0)).getUserBookings(anyLong(), anyString(), anyInt(), anyInt());
    }


}
