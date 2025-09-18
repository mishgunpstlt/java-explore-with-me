package ru.practicum.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.userDto.NewUserDto;
import ru.practicum.dto.userDto.UserDto;
import ru.practicum.dto.userDto.UserMapper;
import ru.practicum.exception.AlreadyExistsException;
import ru.practicum.model.user.User;
import ru.practicum.repository.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        List<User> users;
        if (ids != null && !ids.isEmpty()) {
            users = userRepository.findAllByIdIn(ids, pageRequest);
        } else {
            users = userRepository.findAll(pageRequest).toList();
        }
        return users.stream().map(UserMapper::toDto).toList();
    }

    @Override
    public UserDto addUser(NewUserDto userDto) {
        existsEmail(userDto.getEmail());
        User user = UserMapper.toNewEntity(userDto);
        log.info("Добавлен пользователь: " + user);
        return UserMapper.toDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long userId) {
        existsUser(userId);
        userRepository.deleteById(userId);
    }

    private void existsUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User with id=" + userId + " was not found");
        }
    }

    private void existsEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new AlreadyExistsException("could not execute statement; SQL [n/a]; constraint [uq_email]; " +
                    "nested exception is org.hibernate.exception.ConstraintViolationException: " +
                    "could not execute statement");
        }
    }
}
