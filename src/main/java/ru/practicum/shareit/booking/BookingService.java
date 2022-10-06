package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingRequestDto bookingRequestDto, Long userId);

    BookingDto updateBooking(Long userId, Long bookingId, Boolean available);

    BookingDto getBooking(Long userId, Long bookingId);

    List<BookingDto> getAllByUser(Long userId, String state);

    List<BookingDto> getAllByOwner(Long userId, String state);
}
