package ru.practicum.service.user;

import ru.practicum.dto.userDto.NewUserDto;
import ru.practicum.dto.userDto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    UserDto addUser(NewUserDto userDto);

    void deleteUser(Long userId);
}
