package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DataNotFound;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }


    @Override
    public UserDto create(UserDto userDto) {
        return UserMapper.toUserDto(repository.save(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto update(UserDto userDto, Long userId) {
        User userFromData = repository.findById(userId).orElseThrow(() -> new DataNotFound(
                "Пользователь не найден"));
        return UserMapper.toUserDto(repository.save(validationUser(
                UserMapper.toUser(userId, userDto), userFromData)));
    }

    @Override
    public void delete(Long userId) {
        repository.deleteById(userId);
    }

    @Override
    public UserDto getById(Long userId) {
        return UserMapper.toUserDto(repository.findById(userId).orElseThrow(() -> new DataNotFound(
                "Пользователь не найден")));
    }

    @Override
    public List<UserDto> getAll() {
        return repository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    private User validationUser(User user, User userFromData) {
        if (user.getName() != null) {
            userFromData.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userFromData.setEmail(user.getEmail());
        }
        return userFromData;
    }
}
