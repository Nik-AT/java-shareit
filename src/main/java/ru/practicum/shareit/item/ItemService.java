package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InfoCommentDto;
import ru.practicum.shareit.item.dto.InfoItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    InfoItemDto createItem(ItemDto itemDto, Long ownerId);

    InfoItemDto updateItem(ItemDto itemDto, Long ownerId, Long itemId);

    InfoItemDto getItemById(Long itemId, Long userId);

    List<InfoItemDto> getAllItemsByOwnerId(Long ownerId);

    List<InfoItemDto> searchItems(String text);

    InfoCommentDto createComment(Long itemId, Long userId, CommentDto commentDto);
}
