package ru.practicum.shareit.userTest;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.TestObj;
import ru.practicum.shareit.exceptions.DataNotFound;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    private UserServiceImpl userService;
    private UserRepository userRepository;
    private User user = TestObj.getUser1();
    private UserDto userDto = TestObj.getUserDto1();
    private UserDto userDtoError = TestObj.getUserDtoError();

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void createUser() {
        when(userRepository.save(UserMapper.toUser(userDto)))
                .thenReturn(user);
        Assertions.assertEquals(userService.create(userDto), userDto);
    }

    @Test
    void update() {
        when(userRepository.save(UserMapper.toUser(userDto)))
                .thenReturn(user);
        Assertions.assertEquals(userDto, userService.create(userDto));
        when(userRepository.save(UserMapper.toUser(userDtoError)))
                .thenThrow(new DataNotFound(String.format("Пользователь с id %d в базе данных не обнаружен", 777)));
        DataNotFound exception = Assertions.assertThrows(
                DataNotFound.class,
                () -> userService.create(userDtoError));
        Assertions.assertEquals(exception.getMessage(), "Пользователь с id 777 в базе данных не обнаружен");
    }

    @Test
    void deleteUser() {
        userService.delete(5L);
        Mockito.verify(userRepository, Mockito.times(1))
                .deleteById(anyLong());
    }

    @Test
    void getUserById() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Assertions.assertEquals(userDto, userService.getById(1L));
        when(userRepository.findById(777L))
                .thenThrow(new DataNotFound(String.format("Пользователь с id %d в базе данных не обнаружен", 777)));
        DataNotFound exception = Assertions.assertThrows(
                DataNotFound.class,
                () -> userService.getById(777L));
        Assertions.assertEquals(exception.getMessage(), "Пользователь с id 777 в базе данных не обнаружен");
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll())
                .thenReturn(new ArrayList<>(Collections.singleton(user)));
        Assertions.assertEquals(userService.getAll(), new ArrayList<>(Collections.singleton(userDto)));
    }
}
