package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InfoBookingDto;
import ru.practicum.shareit.exceptions.NotFoundException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
    public List<InfoBookingDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(defaultValue = "ALL") String state,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                             int from,
                                             @Positive @RequestParam(name = "size", defaultValue = "10")
                                             int size) {
        if (from < 0) throw new NotFoundException("Отрицательное");
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("start").descending());
        return bookingService.getAllByUser(userId, state, pageRequest);
    }

    @GetMapping("/owner")
    public List<InfoBookingDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(defaultValue = "ALL") String state,
                                              @RequestParam(name = "from", defaultValue = "0")
                                              int from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10")
                                              int size) {
        if (from < 0) throw new NotFoundException("Отрицательное");
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("start").descending());
        return bookingService.getAllByOwner(userId, state, pageRequest);
    }
}
