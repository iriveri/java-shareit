package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ItemRequestDataDto extends ItemRequestDto {
    List<ItemResponseDto> items;
}
