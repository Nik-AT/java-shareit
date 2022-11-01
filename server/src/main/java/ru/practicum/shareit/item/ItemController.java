package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.Create;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InfoCommentDto;
import ru.practicum.shareit.item.dto.InfoItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

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
                              @RequestHeader(name = "X-Sharer-User-Id") Long ownerId) {
        log.info("Запрос на добавление предмета от пользователя: {}", ownerId);
        return itemService.createItem(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public InfoItemDto update(
            @PathVariable Long itemId,
            @RequestHeader(name = "X-Sharer-User-Id", required = false) Long ownerId,
            @RequestBody ItemDto itemDto) {
        log.info("Запрос на обновление предмета: {} , пользователя: {}", itemId, ownerId);
        if (ownerId == null) {
            throw new NotFoundException("Не верный запрос, отсутствует ИД владельца");
        }
        itemDto.setId(itemId);
        return itemService.updateItem(itemDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public InfoItemDto getItemById(@PathVariable Long itemId,
                                   @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Просмотр предметов пользователя: {} ", userId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<InfoItemDto> getAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(name = "from", defaultValue = "0")
                                             int from,
                                             @RequestParam(name = "size", defaultValue = "10")
                                             int size) {
        log.info("Все предметы пользователя: {}", userId);
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        return itemService.getAllItemsByOwnerId(userId, pageRequest);
    }

    @GetMapping("/search")
    public List<InfoItemDto> searchItems(@RequestParam String text,
                                         @RequestParam(name = "from", defaultValue = "0")
                                         int from,
                                         @RequestParam(name = "size", defaultValue = "10")
                                         int size) {
        log.info("Запрос на поиск предмета: {}", text);
        if (text.equals("")) {
            return new ArrayList<>();
        }
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        return itemService.searchItems(text, pageRequest);
    }

    @PostMapping("/{itemId}/comment")
    public InfoCommentDto addComment(@PathVariable Long itemId,
                                     @RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestBody CommentDto commentDto) {
        log.info("Добавление комментария пользователем: {}", userId);
        return itemService.createComment(itemId, userId, commentDto);
    }
}
