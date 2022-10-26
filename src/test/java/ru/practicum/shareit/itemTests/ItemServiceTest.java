package ru.practicum.shareit.itemTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.TestObj;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.DataNotFound;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InfoItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemServiceTest {

    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private ItemMapper mapper;
    private CommentMapper commentMapper;
    private ItemServiceImpl itemService;

    private User user1 = TestObj.getUser1();
    private UserDto userError = TestObj.getUserDtoError();
    private ItemDto itemDto1 = TestObj.getItemDto1();
    private Item item1 = TestObj.getItem1();
    private InfoItemDto infoItemDto1 = TestObj.getInfoItemDto1();
    private InfoItemDto infoItemDtoToOwner = TestObj.itemDtoToOwner();
    private Booking futureBooking = TestObj.futureBooking();
    private Booking pastBooking = TestObj.pastBooking();
    private Booking rejectedBooking = TestObj.rejectedBooking();
    private CommentDto commentDto = TestObj.commentDto();


    @BeforeEach
    void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        mapper = mock(ItemMapper.class);
        commentMapper = mock(CommentMapper.class);
        itemService = new ItemServiceImpl(itemRepository, mapper, userRepository, bookingRepository,
                commentRepository, commentMapper);
    }

    @Test
    void createItem() {
        userValidation();
        when(mapper.toItem(itemDto1, 1L)).thenReturn(item1);
        when(mapper.toInfoItemDto(item1)).thenReturn(infoItemDto1);
        when(itemRepository.save(item1)).thenReturn(item1);
        Assertions.assertEquals(infoItemDto1, itemService.createItem(itemDto1, user1.getId()));
    }

    @Test
    void updateItem() {
        userValidation();
        itemValidation();
        when(mapper.toInfoItemDto(item1)).thenReturn(infoItemDto1);
        when(itemRepository.save(item1)).thenReturn(item1);
        Assertions.assertEquals(itemService.updateItem(itemDto1, 1L), infoItemDto1);
        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.updateItem(itemDto1, 777L));
        Assertions.assertEquals("Не верно указан собственник", exception.getMessage());
    }

    @Test
    void getItemById() {
        userValidation();
        itemValidation();
        when(mapper.toInfoItemDto(any())).thenReturn(infoItemDto1);
        when(mapper.toInfoItemDtoNotOwner(any())).thenReturn(infoItemDtoToOwner);
        Assertions.assertEquals(itemService.getItemById(1L, 1L), infoItemDto1);
        Assertions.assertEquals(itemService.getItemById(1L, 333L), infoItemDtoToOwner);
    }

    @Test
    void getAllItemsByOwnerId() {
        userValidation();
        when(itemRepository.findByOwnerId(any(), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(item1)));
        when(mapper.toInfoItemDto(any())).thenReturn(infoItemDto1);
        Assertions.assertEquals(new ArrayList<>(List.of(infoItemDto1)),
                itemService.getAllItemsByOwnerId(1L, PageRequest.of(0, 10)));
    }

    @Test
    void searchItems() {
        when(itemRepository.findByNameContainsOrDescriptionContainsIgnoreCase(any(), any(), any()))
                .thenReturn(new ArrayList<>(Collections.singletonList(item1)));
        when(mapper.toInfoItemDto(any())).thenReturn(infoItemDto1);
        Assertions.assertEquals(new ArrayList<>(List.of(infoItemDto1)),
                itemService.searchItems("text", PageRequest.of(0, 10)));
    }

    @Test
    void createComment() {
        userValidation();
        itemValidation();
        when(bookingRepository.findBookingsByBookerIdAndItemId(3L, 3L))
                .thenReturn(new ArrayList<>(Arrays.asList(futureBooking, rejectedBooking)));
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.createComment(3L, 3L, commentDto));
        Assertions.assertEquals("Ошибка, коммент может оставить пользователь который брал предмет в аренду",
                exception.getMessage());

        when(bookingRepository.findBookingsByBookerIdAndItemId(1L, 3L))
                .thenReturn(new ArrayList<>(Arrays.asList(futureBooking, pastBooking, rejectedBooking)));
        when(commentMapper.toComment(anyLong(), anyLong(), any()))
                .thenReturn(TestObj.comment());
        when(commentRepository.save(any()))
                .thenReturn(TestObj.comment());
        Assertions.assertEquals(itemService.createComment(1L, 3L, commentDto),
                TestObj.infoCommentDto());
    }

    void userValidation() {
        when(userRepository.findById(anyLong()))
                .thenAnswer(invocationOnMock -> {
                    Long userId = invocationOnMock.getArgument(0, Long.class);
                    if (userId == 777L) {
                        throw new DataNotFound(
                                String.format("Пользователь с id %d в базе данных не обнаружен", 777));
                    } else {
                        return Optional.of(user1);
                    }
                });
        DataNotFound exception = Assertions.assertThrows(
                DataNotFound.class,
                () -> itemService.createItem(itemDto1, userError.getId()));
        Assertions.assertEquals("Пользователь с id 777 в базе данных не обнаружен",
                exception.getMessage());
    }

    void itemValidation() {
        when(itemRepository.findById(anyLong()))
                .thenAnswer(invocationOnMock -> {
                    Long id = invocationOnMock.getArgument(0, Long.class);
                    if (id == 777) {
                        throw new DataNotFound(
                                String.format("Вещи с id %d в базе данных не обнаружен", 777));
                    } else {
                        return Optional.of(item1);
                    }
                });
    }
}