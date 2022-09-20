package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.user.exception.EmailException;
import ru.practicum.shareit.user.exception.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private long lastId = 1;

    @Override
    public User save(User user) {
        validation(user);
        getId(user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(Long userId) {
        return users.get(userId);
    }

    @Override
    public void delete(Long userId) {
        users.remove(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    private void getId(User user) {
        if (user.getId() == null) {
            user.setId(lastId);
            lastId++;
        }
    }

    private void validation(User user) {
        if (!StringUtils.hasText(user.getName())) throw new ValidationException("Нет имени");
        if (!StringUtils.hasText(user.getEmail())) throw new ValidationException("Нет email");
        if (validationDuplicate(user)) throw new EmailException("Не корректный email");
    }

    private boolean validationDuplicate(User user) {
        final String email = user.getEmail();
        final Long userId = user.getId();
        List<User> allUsers = this.users.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .filter(u -> !u.getId().equals(userId))
                .collect(Collectors.toList());
        return !allUsers.isEmpty();
    }
}
