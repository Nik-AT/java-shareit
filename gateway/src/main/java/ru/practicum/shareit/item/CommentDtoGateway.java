package ru.practicum.shareit.item;


import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDtoGateway {

    private Long id;
    @NotBlank
    private String text;

    private Long itemId;

    private Long authorId;

    private LocalDateTime created;


}
