package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        userRepository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        userDto.setId(userId);
        User userByRep = userRepository.getUserById(userId);
        if (userByRep == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        User user = UserMapper.matchUser(userDto, userByRep);
        userRepository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto getUserDtoById(Long userId) {
        User repoUser = userRepository.getUserById(userId);
        if (repoUser == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        return UserMapper.toUserDto(repoUser);
    }

    @Override
    public void delete(Long userId) {
        userRepository.delete(userId);
    }

    @Override
    public List<UserDto> getAllUsersDto() {
        List<User> users = userRepository.getAllUsers();
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
