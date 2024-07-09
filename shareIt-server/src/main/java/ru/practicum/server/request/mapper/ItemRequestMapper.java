package ru.practicum.server.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.practicum.common.request.dto.ItemRequestDto;
import ru.practicum.common.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.server.item.mapper.ItemMapper;
import ru.practicum.server.request.model.ExtendedItemRequest;
import ru.practicum.server.request.model.ItemRequest;


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
