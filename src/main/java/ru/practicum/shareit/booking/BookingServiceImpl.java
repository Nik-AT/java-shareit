package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InfoBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.States;
import ru.practicum.shareit.exceptions.DataNotFound;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper mapper;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, BookingMapper mapper,
                              ItemRepository itemRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.mapper = mapper;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public InfoBookingDto create(BookingDto bookingDto, Long bookerId) {
        bookingValidation(bookingDto, bookerId);
        Booking booking = mapper.toBooking(bookingDto, bookerId);
        booking.setStates(States.WAITING);
        return BookingMapper.toInfoBookingDto(bookingRepository.save(booking));
    }

    public InfoBookingDto updateBooking(Long bookingId, Boolean approved, Long ownerId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new DataNotFound(
                "Бронь не обнаружена"));
        if (booking.getStates().equals(States.APPROVED)) {
            throw new NotFoundException("Изменение статуса невозможно");
        }
        if (!ownerId.equals(booking.getItem().getOwner().getId())) {
            throw new ValidationException("Подтвердить запрос может только владелей вещи");
        }
        if (approved) {
            booking.setStates(States.APPROVED);
        } else {
            booking.setStates(States.REJECTED);
        }
        return BookingMapper.toInfoBookingDto(bookingRepository.save(booking));
    }

    public List<InfoBookingDto> getAllByUser(Long userId, String state) {
        userValidation(userId);
        try {
            States.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(String.format("{\"error\": \"Unknown state: %s\" }", state));
        }
        return setBookingStatus(bookingRepository.findBookingsByBooker(userId), state).stream()
                .map(BookingMapper::toInfoBookingDto).collect(Collectors.toList());
    }

    public InfoBookingDto getBooking(Long userId, Long bookingId) {
        userValidation(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new DataNotFound(
                "Бронироване не обнаружено"));
        if (!userId.equals(booking.getItem().getOwner().getId()) && !userId.equals(booking.getBooker().getId())) {
            throw new ValidationException("Информацию по бронированию может быть предоставлена только владельцу");
        }
        return BookingMapper.toInfoBookingDto(booking);
    }

    public List<InfoBookingDto> getAllByOwner(Long userId, String state) {
        userValidation(userId);
        try {
            States.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(String.format("{\"error\": \"Unknown state: %s\" }", state));
        }
        return setBookingStatus(bookingRepository.findBookingsByOwner(userId), state).stream()
                .map(BookingMapper::toInfoBookingDto).collect(Collectors.toList());
    }


    private void bookingValidation(BookingDto bookingDto, Long bookerId) {
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new DataNotFound(
                "Предмет не найден"));
        if (!item.getAvailable()) {
            throw new NotFoundException("Бронирование предмета не возможна");
        }
        LocalDateTime timeNow = LocalDateTime.now();
        if (bookingDto.getStart().isBefore(timeNow)
                || bookingDto.getEnd().isBefore(timeNow)
                || bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new NotFoundException("Не найдено");
        }
        userRepository.findById(bookerId).orElseThrow(() -> new DataNotFound(
                "Полльзователь не найден"));
        if (item.getOwner().getId().equals(bookerId)) {
            throw new ValidationException("Отказано в доступе к предмету");
        }
    }

    private void userValidation(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new DataNotFound(
                "Пользователь не найден"));
    }

    private List<Booking> setBookingStatus(List<Booking> bookings, String state) {
        if (States.valueOf(state.toUpperCase()).equals(States.WAITING)) {
            return bookings.stream().filter((b) -> b.getStates().equals(States.WAITING))
                    .collect(Collectors.toList());
        } else if (States.valueOf(state.toUpperCase()).equals(States.REJECTED)) {
            return bookings.stream().filter((b) -> b.getStates().equals(States.REJECTED))
                    .collect(Collectors.toList());
        } else if (States.valueOf(state.toUpperCase()).equals(States.FUTURE)) {
            return bookings.stream().filter((b) -> LocalDateTime.now().isBefore(b.getStart()))
                    .collect(Collectors.toList());
        } else if (States.valueOf(state.toUpperCase()).equals(States.PAST)) {
            return bookings.stream().filter((b) -> LocalDateTime.now().isAfter(b.getEnd()))
                    .collect(Collectors.toList());
        } else if (States.valueOf(state.toUpperCase()).equals(States.CURRENT)) {
            return bookings.stream().filter((b) -> LocalDateTime.now().isAfter(b.getStart())
                    && LocalDateTime.now().isBefore(b.getEnd())).collect(Collectors.toList());
        }
        return bookings;
    }
}
