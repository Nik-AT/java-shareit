package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long id);

    ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto);

    ItemDto getById(Long id, Long userId);

    List<ItemDto> getAllUserItems(Long ownerId);

    List<ItemDto> searchItems(String text);

    CommentDto addComment(Long itemId, Long userId, CommentDto commentDto);
}
