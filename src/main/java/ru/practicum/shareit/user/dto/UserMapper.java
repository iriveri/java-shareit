package ru.practicum.shareit.user.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.model.User;

@Mapper
public interface UserMapper {
    UserDto itemToItemDto(User user);
    User dtoItemToItem(UserDto userDto);
}