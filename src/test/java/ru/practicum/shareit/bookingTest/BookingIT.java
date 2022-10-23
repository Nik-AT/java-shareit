package ru.practicum.shareit.bookingTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.TestObj;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringJUnitConfig({ShareItApp.class, ItemServiceImpl.class, UserServiceImpl.class, BookingServiceImpl.class})
public class BookingIT {

    private final EntityManager em;
    private final ItemServiceImpl itemService;
    private final UserServiceImpl userService;
    private final BookingServiceImpl bookingService;

    @Test
    void createBooking() {
        UserDto userDto1 = TestObj.getUserDto1();
        userService.create(userDto1);
        UserDto userDto2 = TestObj.getUserDto2();
        userService.create(userDto2);
        ItemDto itemDto = TestObj.getItemDto3();
        itemDto.setRequestId(null);
        itemService.createItem(itemDto, 1L);
        itemService.createItem(itemDto, 1L);
        itemService.createItem(itemDto, 2L);
        BookingDto bookingDto = TestObj.futureBookingDto1();
        bookingService.create(bookingDto, 1L);
        Booking booking = TestObj.futureBooking();

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking queryBooking = query
                .setParameter("id", 1L)
                .getSingleResult();
        Assertions.assertEquals(booking, queryBooking);
    }
}
