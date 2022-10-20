package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto, Long userid);

    UserDto getById(Long id);

    List<UserDto> getAll();

    void delete(Long id);
}
