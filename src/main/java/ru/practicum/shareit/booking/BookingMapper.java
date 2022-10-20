package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InfoBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.DataNotFound;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

@Component
public class BookingMapper {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingMapper(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public Booking toBooking(BookingDto bookingDto, Long bookerId) {
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new DataNotFound(
                "Предмет не обнаружен"));
        User booker = userRepository.findById(bookerId).orElseThrow(() -> new DataNotFound(
                "Позльзователь не найден"));
        return new Booking(booker, item, bookingDto.getStart(), bookingDto.getEnd());
    }

    public static InfoBookingDto toInfoBookingDto(Booking booking) {
        return new InfoBookingDto(booking.getId(),
                booking.getBooker(),
                booking.getItem(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStates());
    }
}
