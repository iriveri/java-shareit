package ru.practicum.shareit.server.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.server.booking.dto.BookingMapper;
import ru.practicum.shareit.server.item.model.ExtendedItem;
import ru.practicum.shareit.server.item.model.Item;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {CommentMapper.class, BookingMapper.class})
public interface ItemMapper {
    ItemDto toDto(Item item);

    Item toItem(ItemDto itemDto);

    @Mapping(source = "lastBooking", target = "lastBooking")
    @Mapping(source = "nextBooking", target = "nextBooking")
    @Mapping(source = "comments", target = "comments")
    ExtendedItemDto toExtendedDto(ExtendedItem item);
}