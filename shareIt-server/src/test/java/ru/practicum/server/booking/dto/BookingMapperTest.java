package ru.practicum.server.booking.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.practicum.common.booking.dto.BookingRequestDto;
import ru.practicum.common.booking.dto.BookingResponseDto;
import ru.practicum.common.booking.dto.BookingShortDto;
import ru.practicum.common.booking.model.BookingStatus;
import ru.practicum.server.booking.mapper.BookingMapper;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JsonTest
class BookingMapperTest {
    @Autowired
    private BookingMapper bookingMapper;


    @TestConfiguration
    static class Config {
        @Bean
        public BookingMapper bookingMapper() {
            return Mappers.getMapper(BookingMapper.class);
        }
    }

    @Test
    public void testToBooking() {
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(1L);
        bookingRequestDto.setStart(LocalDateTime.now());
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(1));

        Booking booking = bookingMapper.toBooking(bookingRequestDto);

        assertNotNull(booking);
        Assertions.assertEquals(bookingRequestDto.getItemId(), booking.getItem().getId());
        assertEquals(bookingRequestDto.getStart(), booking.getStart());
        assertEquals(bookingRequestDto.getEnd(), booking.getEnd());
    }

    @Test
    public void testToRequestDto() {
        Item item = new Item();
        item.setId(1L);

        User booker = new User();
        booker.setId(1L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.APPROVED);

        BookingRequestDto bookingRequestDto = bookingMapper.toRequestDto(booking);

        assertNotNull(bookingRequestDto);
        Assertions.assertEquals(booking.getItem().getId(), bookingRequestDto.getItemId());
    }

    @Test
    public void testToResponseDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("ItemName");

        User booker = new User();
        booker.setId(1L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.APPROVED);

        BookingResponseDto bookingResponseDto = bookingMapper.toResponseDto(booking);

        assertNotNull(bookingResponseDto);
        Assertions.assertEquals(booking.getItem().getId(), bookingResponseDto.getItem().getId());
        Assertions.assertEquals(booking.getItem().getName(), bookingResponseDto.getItem().getName());
        Assertions.assertEquals(booking.getBooker().getId(), bookingResponseDto.getBooker().getId());
    }

    @Test
    public void testToShortDto() {
        User booker = new User();
        booker.setId(1L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.APPROVED);

        BookingShortDto bookingShortDto = bookingMapper.toShortDto(booking);

        assertNotNull(bookingShortDto);
        Assertions.assertEquals(booking.getBooker().getId(), bookingShortDto.getBookerId());
    }
}