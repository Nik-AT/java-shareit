package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InfoBookingDto;
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
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new DataNotFound(
                String.format("Вещь с id %d в базе данных не обнаружен", bookingDto.getItemId())));
        if (!item.getAvailable()) {
            throw new NotFoundException("Бронирование данной вещи невозможно, статус вещи 'занята'");
        }
        LocalDateTime timeNow = LocalDateTime.now();
        if (bookingDto.getStart().isBefore(timeNow)
                || bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new NotFoundException("Некорректно указаны сроки бронирования");
        }
        if (item.getOwner().getId().equals(bookerId)) {
            throw new ValidationException("Владелец не может бранировать свою вещь");
        }
        User booker = userRepository.findById(bookerId).orElseThrow(() -> new DataNotFound(
                String.format("Пользователь с id %d в базе данных не обнаружен", bookerId)));
        Booking booking = mapper.toBooking(bookingDto, item, booker);
        booking.setState(State.WAITING);
        return BookingMapper.toInfoBookingDto(bookingRepository.save(booking));
    }

    public InfoBookingDto updateBooking(Long bookingId, Boolean approved, Long ownerId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new DataNotFound(
                "Бронь не обнаружена"));
        if (booking.getState().equals(State.APPROVED)) {
            throw new NotFoundException("Изменение статуса невозможно");
        }
        if (!ownerId.equals(booking.getItem().getOwner().getId())) {
            throw new ValidationException("Подтвердить запрос может только владелей вещи");
        }
        if (approved) {
            booking.setState(State.APPROVED);
        } else {
            booking.setState(State.REJECTED);
        }
        return BookingMapper.toInfoBookingDto(bookingRepository.save(booking));
    }

    public List<InfoBookingDto> getAllByUser(Long userId, String state, PageRequest pageRequest) {
        userValidation(userId);
        try {
            State.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(String.format("{\"error\": \"Unknown state: %s\" }", state));
        }
        return setBookingStatus(bookingRepository.findBookingsByBookerId(userId, pageRequest), state).stream()
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

    public List<InfoBookingDto> getAllByOwner(Long userId, String state, PageRequest pageRequest) {
        userValidation(userId);
        try {
            State.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(String.format("{\"error\": \"Unknown state: %s\" }", state));
        }
        return setBookingStatus(bookingRepository.findBookingsByItemOwnerId(userId, pageRequest), state).stream()
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
        if (State.valueOf(state.toUpperCase()).equals(State.WAITING)) {
            return bookings.stream().filter((b) -> b.getState().equals(State.WAITING))
                    .collect(Collectors.toList());
        } else if (State.valueOf(state.toUpperCase()).equals(State.REJECTED)) {
            return bookings.stream().filter((b) -> b.getState().equals(State.REJECTED))
                    .collect(Collectors.toList());
        } else if (State.valueOf(state.toUpperCase()).equals(State.FUTURE)) {
            return bookings.stream().filter((b) -> LocalDateTime.now().isBefore(b.getStart()))
                    .collect(Collectors.toList());
        } else if (State.valueOf(state.toUpperCase()).equals(State.PAST)) {
            return bookings.stream().filter((b) -> LocalDateTime.now().isAfter(b.getEnd()))
                    .filter((b) -> b.getState().equals(State.APPROVED))
                    .collect(Collectors.toList());
        } else if (State.valueOf(state.toUpperCase()).equals(State.CURRENT)) {
            return bookings.stream().filter((b) -> LocalDateTime.now().isAfter(b.getStart())
                            && LocalDateTime.now().isBefore(b.getEnd()))
                    .collect(Collectors.toList());
        }
        return bookings;
    }
}
