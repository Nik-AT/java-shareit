package ru.practicum.shareit.userTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.TestObj;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringJUnitConfig({ShareItServer.class, UserServiceImpl.class})
public class UserIT {

    private final EntityManager em;
    private final UserService userService;


    @Test
    void getUserById() {
        User user = TestObj.getUser1();
        UserDto userDto = TestObj.getUserDto1();
        userDto.setId(null);
        userService.create(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User queryUser = query
                .setParameter("id", 1L)
                .getSingleResult();
        Assertions.assertEquals(user, queryUser);
    }
}
