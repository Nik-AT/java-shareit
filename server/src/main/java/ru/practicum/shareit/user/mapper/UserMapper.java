package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

@Component
public class UserMapper {


    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static User toUser(UserDto userDto) {
        return new User(userDto.getName(), userDto.getEmail());
    }

    public static User toUser(Long userId, UserDto userDto) {
        return new User(userId, userDto.getName(), userDto.getEmail());
    }
}
