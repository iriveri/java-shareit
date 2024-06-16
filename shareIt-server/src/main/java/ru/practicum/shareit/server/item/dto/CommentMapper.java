package ru.practicum.shareit.server.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.server.item.model.Comment;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    @Mapping(source = "user.name", target = "authorName")
    CommentDto toDto(Comment comment);

    @Mapping(source = "authorName", target = "user.name")
    Comment toComment(CommentDto commentDto);
}