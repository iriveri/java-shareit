package ru.practicum.server.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.practicum.common.request.dto.ItemResponseDto;
import ru.practicum.server.request.model.ItemResponse;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ItemResponseMapper {
    @Mapping(source = "responseItem.id", target = "id")
    @Mapping(source = "responseItem.name", target = "name")
    @Mapping(source = "responseItem.description", target = "description")
    @Mapping(source = "responseItem.available", target = "available")
    @Mapping(source = "request.id", target = "requestId")
    ItemResponseDto toDto(ItemResponse itemResponse);
}
