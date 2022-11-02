package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.Create;
import ru.practicum.shareit.exception.NullDataException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequestMapping("/items")
@Validated
public class ItemControllerGateway {

    private final ItemClient itemClient;

    @Autowired
    public ItemControllerGateway(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@Validated({Create.class}) @RequestBody ItemDtoGateway itemDto,
                                             @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Запрос на добавление предмета от пользователя: {}", ownerId);
        return itemClient.createItem(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable Long itemId, @RequestBody @Valid ItemDtoGateway itemDto,
                                             @RequestHeader(name = "X-Sharer-User-Id", required = false) Long ownerId) {
        log.info("Запрос на обновление предмета: {} , пользователя: {}", itemId, ownerId);
        if (ownerId == null) {
            throw new NullDataException("Не верный запрос, отсутствует ИД владельца");
        }
        itemDto.setId(itemId);
        return itemClient.updateItem(itemDto, ownerId);

    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Просмотр предметов пользователя: {} ", userId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                       @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                       Integer from,
                                                       @Positive @RequestParam(name = "size", defaultValue = "10")
                                                       Integer size) {
        log.info("Все предметы пользователя: {}", ownerId);
        return itemClient.getAllItemsByOwnerId(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam String text,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                              Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10")
                                              Integer size) {
        log.info("Запрос на поиск предмета: {}", text);
        return itemClient.searchItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Valid @RequestBody CommentDtoGateway commentDto) {
        log.info("Добавление комментария пользователем: {}", userId);
        return itemClient.createComment(itemId, userId, commentDto);
    }

}
