package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {
    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .item(comment.getItemId())
                .author(comment.getAuthorId())
                .created(comment.getCreated())
                .build();
    }

    public static Comment fromDto(CommentDto comment) {
        return Comment.builder()
                .id(comment.getId())
                .text(comment.getText())
                .itemId(comment.getItem())
                .authorId(comment.getAuthor())
                .created(comment.getCreated())
                .build();
    }
}
