package ru.practicum.shareit.bookingTest;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.TestObj;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.exceptions.DataNotFound;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
class BookingServiceTest {

    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingMapper mapper;
    private BookingServiceImpl bookingService;

    @BeforeEach
    void beforeEach() {
        bookingRepository = mock(BookingRepository.class);
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        mapper = mock(BookingMapper.class);
        bookingService = new BookingServiceImpl(bookingRepository, mapper, itemRepository, userRepository);
    }

    @Test
    void createBooking() {
        BookingDto bookingDto = TestObj.futureBookingDto1();
        bookingDto.setItemId(777L);
        Item item = TestObj.getItem3();
        item.setAvailable(false);
        when(itemRepository.findById(any()))
                .thenAnswer(invocationOnMock -> {
                    Long itemId = invocationOnMock.getArgument(0, Long.class);
                    if (itemId == 777) {
                        throw new DataNotFound("???????? ?? id 777 ?? ???????? ???????????? ???? ??????????????????");
                    } else {
                        return Optional.of(item);
                    }
                });
        DataNotFound exception = Assertions.assertThrows(
                DataNotFound.class,
                () -> bookingService.create(bookingDto, 1L));
        Assertions.assertEquals("???????? ?? id 777 ?? ???????? ???????????? ???? ??????????????????",
                exception.getMessage());

        bookingDto.setItemId(3L);
        NotFoundException exception1 = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.create(bookingDto, 1L));
        Assertions.assertEquals("???????????????????????? ???????????? ???????? ????????????????????, ???????????? ???????? '????????????'",
                exception1.getMessage());

        item.setAvailable(true);
        bookingDto.setStart(LocalDateTime.now().minus(Period.ofDays(1)));
        NotFoundException exception2 = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.create(bookingDto, 1L));
        Assertions.assertEquals("?????????????????????? ?????????????? ?????????? ????????????????????????",
                exception2.getMessage());

        bookingDto.setStart(LocalDateTime.of(2023, 10, 1, 12, 0));
        bookingDto.setEnd(LocalDateTime.of(2023, 10, 1, 11, 0));
        NotFoundException exception3 = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.create(bookingDto, 1L));
        Assertions.assertEquals("?????????????????????? ?????????????? ?????????? ????????????????????????",
                exception3.getMessage());

        bookingDto.setEnd(LocalDateTime.of(2023, 10, 2, 12, 0));
        ValidationException exception4 = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.create(bookingDto, 2L));
        Assertions.assertEquals("???????????????? ???? ?????????? ?????????????????????? ???????? ????????",
                exception4.getMessage());

        userValidation();
        DataNotFound exception5 = Assertions.assertThrows(
                DataNotFound.class,
                () -> bookingService.getBooking(1L, 777L));
        Assertions.assertEquals("?????????????????????? ???? ????????????????????",
                exception5.getMessage());

        User booker = TestObj.getUser1();
        Booking booking = TestObj.futureBooking();
        when(mapper.toBooking(bookingDto, item, booker))
                .thenReturn(booking);
        when(bookingRepository.save(booking))
                .thenReturn(booking);

        Assertions.assertEquals(bookingService.create(bookingDto, 1L),
                TestObj.waitingFutureInfoBookingDto1());
    }

    @Test
    void approveBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(TestObj.futureBooking()));
        Booking approvedBooking = TestObj.futureBooking();
        approvedBooking.setState(State.APPROVED);
        when(bookingRepository.save(any())).thenReturn(approvedBooking);
        Assertions.assertEquals(TestObj.approvedFutureInfoBookingDto1(),
                bookingService.updateBooking(1L, true, 2L));

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(TestObj.futureBooking()));
        Booking rejectedBooking = TestObj.futureBooking();
        rejectedBooking.setState(State.REJECTED);
        when(bookingRepository.save(any())).thenReturn(rejectedBooking);
        Assertions.assertEquals(TestObj.rejectedFutureInfoBookingDto1(),
                bookingService.updateBooking(1L, false, 2L));

        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.updateBooking(1L, true, 777L));
        Assertions.assertEquals("?????????????????????? ???????????? ?????????? ???????????? ???????????????? ????????",
                exception.getMessage());

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(TestObj.pastBooking()));
        NotFoundException exception1 = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.updateBooking(1L, true, 2L));
        Assertions.assertEquals("?????????????????? ?????????????? ????????????????????",
                exception1.getMessage());
    }

    @Test
    void getBookingById() {
        userValidation();
        DataNotFound exception = Assertions.assertThrows(
                DataNotFound.class,
                () -> bookingService.getBooking(1L, 777L));
        Assertions.assertEquals("?????????????????????? ???? ????????????????????",
                exception.getMessage());

        when(bookingRepository.findById(anyLong()))
                .thenAnswer(invocationOnMock -> {
                    Long bookingId = invocationOnMock.getArgument(0, Long.class);
                    if (bookingId == 777L) {
                        throw new DataNotFound(
                                String.format("???????????????????????? ?? id %d ?? ???????? ???????????? ???? ????????????????????", 777));
                    } else {
                        return Optional.of(TestObj.futureBooking());
                    }
                });
        DataNotFound exception1 = Assertions.assertThrows(
                DataNotFound.class,
                () -> bookingService.getBooking(777L, 1L));
        Assertions.assertEquals("???????????????????????? ?? id 777 ?? ???????? ???????????? ???? ??????????????????",
                exception1.getMessage());

        ValidationException exception2 = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.getBooking(333L, 1L));
        Assertions.assertEquals("???????????????????? ???? ???????????????????????? ?????????? ???????? ?????????????????????????? ???????????? ??????????????????",
                exception2.getMessage());

        Assertions.assertEquals(bookingService.getBooking(1L, 1L),
                TestObj.futureInfoBookingDto1());
    }

    @Test
    void getBookingsByUserId() {
        userValidation();
        DataNotFound exception = Assertions.assertThrows(
                DataNotFound.class,
                () -> bookingService.getAllByUser(777L, "APPROVED", PageRequest.of(0, 10)));
        Assertions.assertEquals("???????????????????????? ?? id 777 ?? ???????? ???????????? ???? ??????????????????",
                exception.getMessage());

        NotFoundException exception1 = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getAllByUser(1L, "None", PageRequest.of(0, 10)));
        Assertions.assertEquals("{\"error\": \"Unknown state: None\" }",
                exception1.getMessage());

        when(bookingRepository.findBookingsByBookerId(anyLong(), any()))
                .thenReturn(TestObj.bookingsForSetStatus());
        Assertions.assertEquals(bookingService.getAllByUser(1L, "WAITING", PageRequest.of(0, 10)),
                new ArrayList<>(List.of(TestObj.waitingInfoBookingDTO())));
        Assertions.assertEquals(bookingService.getAllByUser(1L, "REJECTED", PageRequest.of(0, 10)),
                new ArrayList<>(List.of(TestObj.rejectedInfoBookingDTO())));
        Assertions.assertEquals(bookingService.getAllByUser(1L, "FUTURE", PageRequest.of(0, 10)),
                new ArrayList<>(Arrays.asList(TestObj.waitingInfoBookingDTO(), TestObj.futureInfoBookingDTO())));
        Assertions.assertEquals(bookingService.getAllByUser(1L, "PAST", PageRequest.of(0, 10)),
                new ArrayList<>(List.of(TestObj.pastInfoBookingDTO())));
        Assertions.assertEquals(bookingService.getAllByUser(1L, "CURRENT", PageRequest.of(0, 10)),
                new ArrayList<>(List.of(TestObj.currentInfoBookingDTO())));
    }

    @Test
    void getBookingsByOwnerId() {
        userValidation();
        DataNotFound exception = Assertions.assertThrows(
                DataNotFound.class,
                () -> bookingService.getAllByOwner(777L, "APPROVED", PageRequest.of(0, 10)));
        Assertions.assertEquals("???????????????????????? ?? id 777 ?? ???????? ???????????? ???? ??????????????????",
                exception.getMessage());

        NotFoundException exception1 = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getAllByOwner(1L, "None", PageRequest.of(0, 10)));
        Assertions.assertEquals("{\"error\": \"Unknown state: None\" }",
                exception1.getMessage());
        when(bookingRepository.findBookingsByItemOwnerId(anyLong(), any()))
                .thenReturn(TestObj.bookingsForSetStatus());
        Assertions.assertEquals(bookingService.getAllByOwner(1L, "WAITING", PageRequest.of(0, 10)),
                new ArrayList<>(List.of(TestObj.waitingInfoBookingDTO())));
        Assertions.assertEquals(bookingService.getAllByOwner(1L, "REJECTED", PageRequest.of(0, 10)),
                new ArrayList<>(List.of(TestObj.rejectedInfoBookingDTO())));
        Assertions.assertEquals(bookingService.getAllByOwner(1L, "FUTURE", PageRequest.of(0, 10)),
                new ArrayList<>(Arrays.asList(TestObj.waitingInfoBookingDTO(), TestObj.futureInfoBookingDTO())));
        Assertions.assertEquals(bookingService.getAllByOwner(1L, "PAST", PageRequest.of(0, 10)),
                new ArrayList<>(List.of(TestObj.pastInfoBookingDTO())));
        Assertions.assertEquals(bookingService.getAllByOwner(1L, "CURRENT", PageRequest.of(0, 10)),
                new ArrayList<>(List.of(TestObj.currentInfoBookingDTO())));
    }

    void userValidation() {
        when(userRepository.findById(anyLong()))
                .thenAnswer(invocationOnMock -> {
                    Long userId = invocationOnMock.getArgument(0, Long.class);
                    if (userId == 777L) {
                        throw new DataNotFound(
                                String.format("???????????????????????? ?? id %d ?? ???????? ???????????? ???? ??????????????????", 777));
                    } else {
                        return Optional.of(TestObj.getUser1());
                    }
                });
    }
}