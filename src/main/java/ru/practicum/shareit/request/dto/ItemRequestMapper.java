package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.model.ExtendedItemRequest;
import ru.practicum.shareit.request.model.ItemRequest;


@Mapper(uses = {ItemMapper.class,ItemResponseMapper.class})
public interface ItemRequestMapper {
    @Mapping(source = "description", target = "description")
    ItemRequest toItemRequest(ItemRequestDto itemRequestDto);

    ItemRequestDto toDto(ItemRequest itemRequest);

    @Mapping(source = "responses", target = "items")
    ItemRequestWithResponsesDto toWithResponsesDto(ExtendedItemRequest extendedItemRequest);


}
