package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto update(Long userId, UserDto userDto);

    UserDto getUserDtoById(Long userId);

    void delete(Long userId);

    List<UserDto> getAllUsersDto();
}
