package ru.practicum.service.comment;

import ru.practicum.dto.commentDto.CommentDto;
import ru.practicum.dto.commentDto.ModerationCommentStatusDto;
import ru.practicum.dto.commentDto.NewCommentDto;

import java.util.List;

public interface CommentService {

    CommentDto addComment(Long userId, Long eventId, NewCommentDto commentDto);

    CommentDto updateComment(Long userId, Long eventId, Long commentId, NewCommentDto commentDto);

    void deleteComment(Long userId, Long eventId, Long commentId);

    List<CommentDto> getPendingComments();

    List<CommentDto> moderationComments(ModerationCommentStatusDto commentStatusDto);

    List<CommentDto> getCommentsByEvent(Long eventId);

    void deleteCommentAdmin(Long commentId);
}
