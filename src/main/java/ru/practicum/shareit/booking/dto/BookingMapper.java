package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.Booking;


@Mapper
public interface BookingMapper {

    @Mapping(source = "itemId", target = "item.id")
    Booking toBooking(BookingRequestDto bookingRequestDto);

    @Mapping(source = "item.id", target = "itemId")
    BookingRequestDto toRequestDto(Booking booking);

    @Mapping(source = "item.id", target = "item.id")
    @Mapping(source = "item.name", target = "item.name")
    @Mapping(source = "booker.id", target = "booker.id")
    BookingResponseDto toResponseDto(Booking booking);

    @Mapping(source = "booker.id", target = "bookerId")
    BookingShortDto toShortDto(Booking booking);

}