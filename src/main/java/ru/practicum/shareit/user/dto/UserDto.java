package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UserDto {
    private Long id;
    @NotNull(message = "e-mail отсутствует")
    @Email(message = "Введён некоректный e-mail")
    private String email;
    @NotNull
    private String name;
}
