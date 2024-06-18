package ru.practicum.shareit.common.user.dto;

import ru.practicum.shareit.common.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDto toDto(User user);

    User toUser(UserDto userDto);
}