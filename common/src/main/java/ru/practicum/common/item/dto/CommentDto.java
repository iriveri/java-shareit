package ru.practicum.common.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class CommentDto {
    private Long id;
    private String authorName;
    @NotBlank
    private String text;
    private LocalDateTime created;
}
