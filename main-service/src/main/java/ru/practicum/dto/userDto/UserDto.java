package ru.practicum.dto.userDto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {

    private String email;

    private Long id;

    private String name;
}
