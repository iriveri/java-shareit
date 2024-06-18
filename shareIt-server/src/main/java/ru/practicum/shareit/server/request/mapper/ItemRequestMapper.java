package ru.practicum.shareit.server.request.mapper;

import ru.practicum.shareit.common.request.dto.ItemRequestDto;
import ru.practicum.shareit.common.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.server.request.model.ExtendedItemRequest;
import ru.practicum.shareit.server.request.model.ItemRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.server.item.mapper.ItemMapper;


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
