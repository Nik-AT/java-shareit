package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {

    User save(User user);

    User getUserById(Long userId);

    void delete(Long userId);

    List<User> getAllUsers();
}
