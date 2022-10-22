package ru.practicum.shareit.item;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InfoCommentDto;
import ru.practicum.shareit.item.dto.InfoItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    InfoItemDto createItem(ItemDto itemDto, Long ownerId);

    InfoItemDto updateItem(ItemDto itemDto, Long ownerId);

    InfoItemDto getItemById(Long itemId, Long userId);

    List<InfoItemDto> getAllItemsByOwnerId(Long ownerId, PageRequest pageRequest);

    List<InfoItemDto> searchItems(String text, PageRequest pageRequest);

    InfoCommentDto createComment(Long itemId, Long userId, CommentDto commentDto);
}
