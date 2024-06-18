package ru.practicum.shareit.common.request.dto;

import ru.practicum.shareit.common.request.model.ExtendedItemRequest;
import ru.practicum.shareit.common.request.model.ItemRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.common.item.dto.ItemMapper;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ItemMapper.class, ItemResponseMapper.class})
public interface ItemRequestMapper {
    @Mapping(source = "description", target = "description")
    ItemRequest toItemRequest(ItemRequestDto itemRequestDto);

    ItemRequestDto toDto(ItemRequest itemRequest);

    @Mapping(source = "responses", target = "items")
    ItemRequestWithResponsesDto toWithResponsesDto(ExtendedItemRequest extendedItemRequest);


}
