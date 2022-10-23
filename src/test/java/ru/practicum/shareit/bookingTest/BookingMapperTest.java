package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.TestObj;
import ru.practicum.shareit.booking.dto.InfoBookingDto;
import ru.practicum.shareit.booking.model.Booking;

@SpringBootTest
public class BookingMapperTest {
    Booking booking;
    InfoBookingDto infoBookingDto;

    @BeforeEach
    void beforeEach() {
        booking = TestObj.fBooking();
        infoBookingDto = TestObj.futureInfoBookingDTO();
    }

    @Test
    void toRequestDtoTest() {
        Assertions.assertEquals(booking.getBooker(), infoBookingDto.getBooker());
    }
}
