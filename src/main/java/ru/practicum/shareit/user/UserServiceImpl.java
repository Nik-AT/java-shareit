package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DuplicateEmailException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDto create(UserDto userDto) {
        validation(userDto);
        User user = userMapper.fromDto(userDto);
        try {
            return userMapper.toDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("Данный email уже используется");
        }
    }

    public UserDto update(UserDto userDto) {
        Optional<User> repoUser = userRepository.findById(userDto.getId());
        if (repoUser.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        User user = repoUser.get();
        if (userDto.getName() != null && !userDto.getName().isEmpty()) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
            if (!userDto.getEmail().contains("@")) throw new ValidationException("Не корректный email!");
            user.setEmail(userDto.getEmail());
        }
        try {
            return userMapper.toDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("Данный email уже используется");
        }
    }

    public UserDto getById(Long id) {
        Optional<User> repoUser = userRepository.findById(id);
        if (repoUser.isPresent()) {
            return userMapper.toDto(repoUser.get());
        }
        throw new NotFoundException("Пользователь не найден");
    }

    public List<UserDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        userRepository.findById(id)
                .ifPresent(userRepository::delete);
    }

    private void validation(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            throw new ValidationException("Не указан email");
        }
        if (!userDto.getEmail().contains("@")) {
            throw new ValidationException("Не корректный email!");
        }
        if (userDto.getName() == null || userDto.getName().isEmpty()) {
            throw new ValidationException("Не указано имя");
        }
    }
}
