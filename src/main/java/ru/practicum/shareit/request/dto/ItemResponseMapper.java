package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.request.model.ItemResponse;

@Mapper
public interface ItemResponseMapper {
    @Mapping(source = "responseItem.id", target = "id")
    @Mapping(source = "responseItem.name", target = "name")
    @Mapping(source = "responseItem.description", target = "description")
    @Mapping(source = "responseItem.available", target = "available")
    @Mapping(source = "request.id", target = "requestId")
    ItemResponseDto toDto(ItemResponse itemResponse);
}