package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InfoBookingDto;

import java.util.List;

public interface BookingService {

    InfoBookingDto create(BookingDto bookingDto, Long bookerId);

    InfoBookingDto updateBooking(Long bookingId, Boolean approved, Long ownerId);

    InfoBookingDto getBooking(Long userId, Long bookingId);

    List<InfoBookingDto> getAllByUser(Long userId, String state, PageRequest pageRequest);

    List<InfoBookingDto> getAllByOwner(Long userId, String state, PageRequest pageRequest);
}
