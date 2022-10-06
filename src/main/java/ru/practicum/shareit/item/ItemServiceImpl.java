package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final RequestRepository requestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserService userService,
                           RequestRepository requestRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.requestRepository = requestRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    public ItemDto create(ItemDto itemDto, Long userId) {
        validation(itemDto, userId);
        UserDto owner = userService.getById(userId);
        Item item = new Item(
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner.getId(),
                itemDto.getRequest() != null ? itemDto.getRequest().getId() : null);
        return toDto(itemRepository.save(item));
    }

    public ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto) {
        Optional<Item> repoItem = itemRepository.findById(itemId);
        if (repoItem.isEmpty()) {
            throw new NotFoundException("Предмет не найден");
        }
        Item itemToUpdate = repoItem.get();
        updateValidation(userId, itemDto, itemToUpdate);
        return toDto(itemRepository.save(itemToUpdate));
    }

    public ItemDto getById(Long itemId, Long userId) {
        Optional<Item> repoItem = itemRepository.findById(itemId);
        if (repoItem.isEmpty()) {
            throw new NotFoundException("Предмет не найден");
        }
        ItemDto itemDto = toDto(repoItem.get());
        if (itemDto.getOwner().getId().equals(userId)) {
            return setBookings(itemDto);
        }
        return itemDto;
    }

    public List<ItemDto> getAllUserItems(Long ownerId) {
        return itemRepository.searchAllByOwnerId(ownerId)
                .stream()
                .map(this::toDto)
                .map(this::setBookings)
                .collect(Collectors.toList());
    }

    public List<ItemDto> searchItems(String text) {
        if (text.equals("")) return List.of();
        String t = text.toLowerCase().trim();
        return itemRepository.searchByText(t)
                .stream()
                .filter(Item::isAvailable)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        if (itemRepository.findById(itemId).isEmpty()) {
            throw new NotFoundException("Предмет не найден");
        }
        if (commentDto.getText().isEmpty()) {
            throw new ValidationException("Комментарий не верный");
        }
        List<Booking> itemBookings = bookingRepository.findAllByItemId(itemId);
        if (itemBookings.size() == 0) {
            throw new NotFoundException("Предмет не забронирован");
        }
        LocalDateTime now = LocalDateTime.now();
        if (validateItemBooking(itemBookings, userId, now)) {
            Comment comment = commentRepository.save(new Comment(
                    null,
                    commentDto.getText(),
                    itemId,
                    userId,
                    now));
            return commentToDto(comment);
        }
        throw new ValidationException("Не верный комментарий");
    }

    private ItemDto toDto(Item itm) {
        ItemRequest itemRequest = null;
        if (itm.getRequest() != null) {
            Optional<ItemRequest> repoRequest = requestRepository.findById(itm.getRequest());
            if (repoRequest.isPresent()) {
                itemRequest = repoRequest.get();
            } else {
                throw new NotFoundException("Запрос не найден");
            }

        }
        UserDto userDto = userService.getById(itm.getOwner());
        List<Comment> comments = commentRepository.findAllByItemId(itm.getId());
        return new ItemDto(
                itm.getId(),
                itm.getName(),
                itm.getDescription(),
                itm.isAvailable(),
                new ItemDto.User(userDto.getId(), userDto.getName()),
                itemRequest,
                null,
                null,
                comments.size() == 0 ? List.of() : comments.stream()
                        .map(this::commentToDto)
                        .collect(Collectors.toList()));
    }

    private ItemDto setBookings(ItemDto itemDto) {
        List<Booking> allItems = bookingRepository.findAllByItemId(itemDto.getId());
        LocalDateTime now = LocalDateTime.now();
        if (!allItems.isEmpty()) {
            Booking last = allItems.get(0);
            Booking next = allItems.get(allItems.size() - 1);
            for (Booking b : allItems) {
                if (b.getEnd().isBefore(now) && b.getEnd().isAfter(last.getEnd())) {
                    last = b;
                }
                if (b.getStart().isAfter(now) && b.getStart().isBefore(next.getStart())) {
                    next = b;
                }
            }
            itemDto.setLastBooking(last);
            itemDto.setNextBooking(next);
        }
        return itemDto;
    }

    private boolean validateItemBooking(List<Booking> bookings, Long userId, LocalDateTime now) {
        boolean valid = false;
        for (Booking b : bookings) {
            if (b.getBookerId().equals(userId) && b.getEnd().isBefore(now)) {
                valid = true;
            }
        }
        return valid;
    }

    private CommentDto commentToDto(Comment comment) {
        UserDto userDto = userService.getById(comment.getAuthor());
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItem(),
                comment.getAuthor(),
                userDto.getName(),
                comment.getCreated());
    }

    private void validation(ItemDto itemDto, Long userId) {
        if (userId == null) {
            throw new ValidationException("Не верный пользователь");
        }
        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            throw new ValidationException("Пустое имя");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            throw new ValidationException("Пустое описание");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Пропущен параметр");
        }
    }

    private void updateValidation(Long userId, ItemDto itemDto, Item itemToUpdate) {
        if (!itemToUpdate.getOwner().equals(userId)) {
            throw new NotFoundException("Вы не владелец");
        }

        if (itemDto.getName() != null && !itemDto.getName().isEmpty()) {
            itemToUpdate.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isEmpty()) {
            itemToUpdate.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            itemToUpdate.setAvailable(itemDto.getAvailable());
        }
    }
}
