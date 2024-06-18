package ru.practicum.shareit.common.item.dto;

import ru.practicum.shareit.common.booking.dto.BookingMapper;
import ru.practicum.shareit.common.item.model.ExtendedItem;
import ru.practicum.shareit.common.item.model.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

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