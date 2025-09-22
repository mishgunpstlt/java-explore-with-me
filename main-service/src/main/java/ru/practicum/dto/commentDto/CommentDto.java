package ru.practicum.dto.commentDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.model.comment.CommentStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDto {

    private Long id;

    private Long eventId;

    private Long userId;

    private String text;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    private CommentStatus status;

}
