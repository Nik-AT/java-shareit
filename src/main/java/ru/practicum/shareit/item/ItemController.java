package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.Create;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InfoCommentDto;
import ru.practicum.shareit.item.dto.InfoItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public InfoItemDto create(@Validated({Create.class})
                              @RequestBody ItemDto itemDto,
                              @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Запрос на добавление предмета от пользователя: {}", userId);
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public InfoItemDto update(
            @PathVariable Long itemId,
            @RequestHeader(name = "X-Sharer-User-Id", required = false) Long ownerId,
            @RequestBody @Valid ItemDto itemDto) {
        log.info("Запрос на обновление предмета: {} , пользователя: {}", itemId, ownerId);
        return itemService.updateItem(itemDto, ownerId, itemId);
    }

    @GetMapping("/{itemId}")
    public InfoItemDto getItemById(@PathVariable Long itemId,
                                   @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Просмотр предметов пользователя: {} ", userId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<InfoItemDto> getAllUserItems(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Все предметы пользователя: {}", userId);
        return itemService.getAllItemsByOwnerId(userId);
    }

    @GetMapping("/search")
    public List<InfoItemDto> searchItems(@RequestParam(name = "text", defaultValue = "") String text) {
        log.info("Запрос на поиск предмета: {}", text);
        if (text.equals("")) {
            return new ArrayList<>();
        }
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public InfoCommentDto addComment(@PathVariable Long itemId,
                                     @RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestBody CommentDto commentDto) {
        log.info("Добавление комментария пользователем: {}", userId);
        return itemService.createComment(itemId, userId, commentDto);
    }
}
