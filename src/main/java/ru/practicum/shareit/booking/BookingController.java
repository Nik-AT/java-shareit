package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InfoBookingDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@Slf4j
public class BookingController {
    @Autowired
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public InfoBookingDto create(@Valid @RequestBody BookingDto bookingDto,
                                 @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        log.info("Создан запрос на бронирование предмета от пользователя : {},", bookerId);
        return bookingService.create(bookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public InfoBookingDto updateBooking(@RequestParam Boolean approved,
                                        @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                        @PathVariable Long bookingId) {
        log.info("Запрос на подтверждение брони предмета от пользователя : {}", ownerId);
        return bookingService.updateBooking(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public InfoBookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        log.info("Запрос от пользователя: {}", userId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<InfoBookingDto> getAllByUser(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "state", defaultValue = "ALL", required = false) String state) {
        log.info("Запрос на просмотр бронирований пользователя: {}", userId);
        return bookingService.getAllByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<InfoBookingDto> getAllByOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "state", defaultValue = "ALL", required = false) String state) {
        log.info("Просмотр владельцем: {} его забронированных предметов", userId);
        return bookingService.getAllByOwner(userId, state);
    }
}
