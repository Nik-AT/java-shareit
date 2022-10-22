package ru.practicum.shareit.itemTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.TestObj;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.InfoItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemMapperTest {

    UserRepository userRepository;
    BookingRepository bookingRepository;
    ItemMapper itemMapper;

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        itemMapper = new ItemMapper(userRepository, bookingRepository);
    }

    @Test
    void toInfoItemDto() {
        Item item = TestObj.getItem3();
        List<Booking> bookingList = new ArrayList<>(Arrays.asList(TestObj.futureBooking(),
                                                                    TestObj.pastBooking()));
        InfoItemDto infoItemDto = new InfoItemDto(item.getId(),
                item.getOwner(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                new ArrayList<>());
        infoItemDto.setLastBooking(InfoItemDto.toBookingDto(TestObj.pastBooking()));
        infoItemDto.setNextBooking(InfoItemDto.toBookingDto(TestObj.futureBooking()));
        when(bookingRepository.findByItemId(any())).thenReturn(bookingList);
        Assertions.assertEquals(itemMapper.toInfoItemDto(item), infoItemDto);
    }

    @Test
    void toInfoItemDtoNotOwner() {
        Item item = TestObj.getItem3();
        InfoItemDto infoItemDto = new InfoItemDto(item.getId(),
                item.getOwner(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                new ArrayList<>());
        Assertions.assertEquals(itemMapper.toInfoItemDtoNotOwner(item), infoItemDto);
    }

    @Test
    void toItem() {
        ItemDto itemDto = TestObj.getItemDto1();
        itemDto.setId(null);
        Item item = TestObj.getItem1();
        item.setId(null);
        item.setComments(null);
        when(userRepository.findById(any())).thenReturn(Optional.of(TestObj.getUser1()));
        Assertions.assertEquals(itemMapper.toItem(itemDto, 1L), item);
    }
}