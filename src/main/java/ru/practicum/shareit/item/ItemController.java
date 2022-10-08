package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    @Autowired
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto create(
            @RequestBody ItemDto itemDto,
            @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Запрос на добавление предмета от пользователя: {}", userId);
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @PathVariable Long itemId,
            @RequestHeader(name = "X-Sharer-User-Id") Long userId,
            @RequestBody ItemDto itemDto) {
        log.info("Запрос на обновление предмета: {} , пользователя: {}", itemId, userId);
        return itemService.updateItem(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId,
                               @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Просмотр предметов пользователя: {} ", userId);
        return itemService.getById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllUserItems(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Все предметы пользователя: {}", userId);
        return itemService.getAllUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(name = "text", defaultValue = "") String text) {
        log.info("Запрос на поиск предмета: {}", text);
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Long itemId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody CommentDto commentDto) {
        log.info("Добавление комментария пользователем: {}", userId);
        return itemService.addComment(itemId, userId, commentDto);
    }
}
