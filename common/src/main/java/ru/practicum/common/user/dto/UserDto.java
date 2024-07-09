package ru.practicum.common.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UserDto {
    private Long id;
    @NotBlank(message = "e-mail отсутствует")
    @Email(message = "Введён некоректный e-mail")
    private String email;
    @NotBlank
    private String name;
}
