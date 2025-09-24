package ru.practicum.dto.commentDto;

import ru.practicum.model.comment.Comment;
import ru.practicum.model.comment.CommentStatus;
import ru.practicum.model.event.Event;
import ru.practicum.model.user.User;

import java.time.LocalDateTime;

public class CommentMapper {

    public static CommentDto toDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getEvent().getId(),
                comment.getUser().getId(),
                comment.getText(),
                comment.getCreated(),
                comment.getStatus());
    }

    public static Comment toNewEntity(NewCommentDto commentDto, Event event, User user) {
        return new Comment(null,
                event,
                user,
                commentDto.getText(),
                LocalDateTime.now(),
                CommentStatus.PENDING);
    }
}
