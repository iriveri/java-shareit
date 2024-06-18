package ru.practicum.shareit.server.booking.mapper;

import ru.practicum.shareit.common.booking.dto.BookingRequestDto;
import ru.practicum.shareit.common.booking.dto.BookingResponseDto;
import ru.practicum.shareit.common.booking.dto.BookingShortDto;
import ru.practicum.shareit.server.booking.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
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