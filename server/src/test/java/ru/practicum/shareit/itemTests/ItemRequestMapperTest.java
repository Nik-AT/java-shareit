package ru.practicum.shareit.itemTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.InfoItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@SpringBootTest
public class ItemRequestMapperTest {

    private ItemRequest itemRequest;
    private User user;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "user 1", "user1@email");
        itemRequest = new ItemRequest(1L, "газонокосилка", LocalDateTime.now());
    }

    @Test
    void toRequestDtoTest() {
        InfoItemRequestDto infoItemRequestDto = ItemRequestMapper.toInfoItemRequestDto(itemRequest);
        Assertions.assertNotNull(infoItemRequestDto);
        Assertions.assertEquals(itemRequest.getDescription(), infoItemRequestDto.getDescription());
    }
}
