package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.Booking;


@Mapper
public interface BookingMapper {
    @Mapping(source = "item.id", target = "itemId")
    BookingRequestDto toBookingRequestDto(Booking booking);

    @Mapping(source = "itemId", target = "item.id")
    Booking toBooking(BookingRequestDto bookingRequestDto);

    @Mapping(source = "item.id", target = "item.id")
    @Mapping(source = "item.name", target = "item.name")
    @Mapping(source = "booker.id", target = "booker.id")
    BookingResponseDto toBookingResponseDto(Booking booking);
}