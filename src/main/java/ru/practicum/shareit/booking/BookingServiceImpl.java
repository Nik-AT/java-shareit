package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.States;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, ItemService itemService, UserService userService, UserRepository userRepository, UserMapper userMapper, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.itemService = itemService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.itemRepository = itemRepository;
    }

    public BookingDto create(BookingRequestDto bookingRequestDto, Long userId) {
        Optional<User> repoUser = userRepository.findById(userId);
        if (repoUser.isPresent()) {
            userMapper.toDto(repoUser.get());
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
        ItemDto itemDto = itemService.getById(bookingRequestDto.getItemId(), null);
        if (itemDto.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Не найден");
        }
        if (!itemDto.getAvailable()) {
            throw new ValidationException("Предмет не доступен");
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startBookingRequest = bookingRequestDto.getStart();
        LocalDateTime endBookingRequest = bookingRequestDto.getEnd();
        if (endBookingRequest.isBefore(now) ||
                endBookingRequest.isBefore(startBookingRequest) ||
                startBookingRequest.isBefore(now)) {
            throw new ValidationException("Не верные данные");
        }
        Booking booking = new Booking(bookingRequestDto.getStart(),
                bookingRequestDto.getEnd(),
                itemDto.getId(), userId,
                BookingStatus.WAITING);
        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    public BookingDto updateBooking(Long userId, Long bookingId, Boolean available) {
        Optional<Booking> repBooking = bookingRepository.findById(bookingId);
        if (repBooking.isEmpty()) {
            throw new NotFoundException("Бронь не найдена");
        }
        Booking booking = repBooking.get();
        if (booking.getBookerId().equals(userId)) {
            throw new NotFoundException("Статус может быть изенен только владельцем");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED) && available) {
            throw new ValidationException("Статус уже одобрен");
        }
        ItemDto itemDto = itemService.getById(booking.getItemId(), null);
        if (!itemDto.getOwner().getId().equals(userId)) {
            throw new ValidationException("Ошибка, вы не владелец");
        }
        booking.setStatus(available ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return setBooking(bookingRepository.save(booking));
    }

    public BookingDto getBooking(Long userId, Long bookingId) {
        Optional<Booking> repBooking = bookingRepository.findById(bookingId);
        if (repBooking.isEmpty()) {
            throw new NotFoundException("Бронирование не найдено");
        }
        Booking booking = repBooking.get();
        ItemDto itemDto = itemService.getById(booking.getItemId(), null);
        if (userId.equals(booking.getBookerId()) || userId.equals(itemDto.getOwner().getId())) {
            return setBooking(booking);
        } else {
            throw new NotFoundException("Ошибка, вы должны быть владельцем");
        }
    }

    @Override
    public List<BookingDto> getAllByUser(Long userId, String state) {
        States states;
        try {
            states = States.valueOf(States.class, state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(String.format("{\"error\": \"Unknown state: %s\" }", state));
        }
        userService.getById(userId);
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        switch (states) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(userId);
                return toCollectionDto(bookings);
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, String.valueOf(BookingStatus.REJECTED));
                return toCollectionDto(bookings);
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, String.valueOf(BookingStatus.WAITING));
                return toCollectionDto(bookings);
            case CURRENT:
                bookings = bookingRepository.findAllByBookerId(userId);
                return toCollectionDto(bookings
                        .stream()
                        .filter(booking -> booking.getStart().isBefore(now) && booking.getEnd().isAfter(now))
                        .collect(Collectors.toList()));
            case PAST:
                bookings = bookingRepository.findAllByBookerId(userId);
                return toCollectionDto(bookings
                        .stream()
                        .filter(booking -> booking.getEnd().isBefore(now))
                        .collect(Collectors.toList()));
            case FUTURE:
                bookings = bookingRepository.findAllByBookerId(userId);
                return toCollectionDto(bookings
                        .stream()
                        .filter(booking -> booking.getStart().isAfter(now))
                        .collect(Collectors.toList()));
            default:
                return List.of();
        }
    }

    @Override
    public List<BookingDto> getAllByOwner(Long userId, String state) {
        States states;
        try {
            states = States.valueOf(States.class, state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(String.format("{\"error\": \"Unknown state: %s\" }", state));
        }
        Collection<ItemDto> allItems = itemService.getAllUserItems(userId);
        if (allItems.size() == 0) {
            throw new NotFoundException("Ошибка, нет предметов");
        }
        LocalDateTime now = LocalDateTime.now();
        List<Booking> allByOwner = bookingRepository.findAllByOwner(userId);

        switch (states) {
            case ALL:
                return toCollectionDto(allByOwner);
            case REJECTED:
                return toCollectionDto(allByOwner.stream()
                        .filter(booking -> booking.getStatus().equals(BookingStatus.REJECTED))
                        .collect(Collectors.toList()));
            case WAITING:

                return toCollectionDto(allByOwner.stream()
                        .filter(booking -> booking.getStatus().equals(BookingStatus.WAITING))
                        .collect(Collectors.toList()));
            case CURRENT:
                return toCollectionDto(allByOwner.stream()
                        .filter(booking -> booking.getStart().isBefore(now) &&
                                booking.getEnd().isAfter(now))
                        .collect(Collectors.toList()));
            case PAST:
                return toCollectionDto(allByOwner.stream()
                        .filter(booking -> booking.getEnd().isBefore(now))
                        .collect(Collectors.toList()));
            case FUTURE:
                return toCollectionDto(allByOwner.stream()
                        .filter(booking -> booking.getStart().isAfter(now))
                        .collect(Collectors.toList()));
            default:
                return List.of();
        }
    }

    private BookingDto setBooking(Booking booking) {
        ItemDto itemDto = itemService.getById(booking.getItemId(), null);
        UserDto userDto = userService.getById(booking.getBookerId());
        BookingDto bookingDto = BookingMapper.toDto(booking);
        bookingDto.setItem(new BookingDto.Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription()));
        bookingDto.setBooker(new BookingDto.User(userDto.getId(), userDto.getName()));
        return bookingDto;
    }

    private List<BookingDto> toCollectionDto(List<Booking> bookings) {
        if (bookings.isEmpty()) throw new NotFoundException("Бронирование не найдено");
        return bookings.stream()
                .map(this::setBooking)
                .sorted((booking1, booking2) -> {
                    if (booking1.getStart().isBefore(booking2.getStart())) return 1;
                    if (booking1.getStart().isAfter(booking2.getStart())) return -1;
                    return 0;
                }).collect(Collectors.toList());
    }
}
