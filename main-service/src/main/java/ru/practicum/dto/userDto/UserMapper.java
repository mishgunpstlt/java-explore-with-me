package ru.practicum.dto.userDto;


import ru.practicum.model.user.User;

public class UserMapper {


    public static UserDto toDto(User user) {
        return new UserDto(user.getEmail(), user.getId(), user.getName());
    }

    public static User toNewEntity(NewUserDto userDto) {
        return new User(null, userDto.getEmail(), userDto.getName());
    }
}
