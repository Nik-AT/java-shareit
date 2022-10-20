package ru.practicum.shareit.item.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.DataNotFound;
import ru.practicum.shareit.item.dto.InfoCommentDto;
import ru.practicum.shareit.item.dto.InfoItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public ItemMapper(UserRepository userRepository, BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    public InfoItemDto toInfoItemDto(Item item) {
        InfoItemDto infoItemDto = getInfoItemDto(item);
        List<Booking> bookingList = bookingRepository.findByItemId(infoItemDto.getId());
        infoItemDto.setLastBooking(InfoItemDto.toBookingDto(findLastBooking(bookingList)));
        infoItemDto.setNextBooking(InfoItemDto.toBookingDto(findNextBooking(bookingList)));
        return infoItemDto;
    }

    public InfoItemDto toInfoItemDtoNotOwner(Item item) {
        InfoItemDto infoItemDto = getInfoItemDto(item);
        infoItemDto.setLastBooking(null);
        infoItemDto.setNextBooking(null);
        return infoItemDto;
    }

    private InfoItemDto getInfoItemDto(Item item) {
        if (item.getComments() == null) {
            item.setComments(new ArrayList<Comment>());
        }
        List<InfoCommentDto> commentsList = item.getComments().stream().map(CommentMapper::toInfoCommentDto)
                .collect(Collectors.toList());
        return new InfoItemDto(item.getId(),
                item.getOwner(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                commentsList);
    }


    public Item toItem(ItemDto itemDto, Long ownerId) {
        User user = userRepository.findById(ownerId).orElseThrow(() -> new DataNotFound(
                "Пользователь не найден"));
        return new Item(user, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
    }

    private Booking findNextBooking(List<Booking> bookingList) {
        return bookingList.stream()
                .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getStart)).orElse(null);
    }

    private Booking findLastBooking(List<Booking> bookingList) {
        return bookingList.stream()
                .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getEnd)).orElse(null);
    }
}
