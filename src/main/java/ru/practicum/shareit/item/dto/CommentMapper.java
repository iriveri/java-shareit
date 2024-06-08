package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Comment;

@Mapper
public interface CommentMapper {
    @Mapping(source = "user.name", target = "authorName")
    CommentDto toDto(Comment comment);

    @Mapping(source = "authorName", target = "user.name")
    Comment toComment(CommentDto commentDto);
}