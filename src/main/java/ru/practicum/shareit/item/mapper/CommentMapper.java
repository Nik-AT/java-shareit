package ru.practicum.shareit.item.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.DataNotFound;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InfoCommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

@Component
public class CommentMapper {

    private final UserRepository userRepository;

    @Autowired
    public CommentMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Comment toComment(Long itemId, Long userId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new DataNotFound(
                "Пользователь не обнаружен"));

        return new Comment(commentDto.getText(), itemId, user, LocalDateTime.now());
    }

    public static InfoCommentDto toInfoCommentDto(Comment comment) {
        return new InfoCommentDto(comment.getId(),
                comment.getText(),
                comment.getItemId(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }
}