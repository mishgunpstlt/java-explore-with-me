package ru.practicum.service.comment;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.commentDto.CommentDto;
import ru.practicum.dto.commentDto.CommentMapper;
import ru.practicum.dto.commentDto.ModerationCommentStatusDto;
import ru.practicum.dto.commentDto.NewCommentDto;
import ru.practicum.exception.NotMetConditionsException;
import ru.practicum.model.comment.Comment;
import ru.practicum.model.comment.CommentStatus;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventStatus;
import ru.practicum.model.user.User;
import ru.practicum.repository.comment.CommentRepository;
import ru.practicum.repository.event.EventRepository;
import ru.practicum.repository.user.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;


    @Override
    public CommentDto addComment(Long userId, Long eventId, NewCommentDto commentDto) {
        User user = existsUser(userId);
        Event event = existsEvent(eventId);
        if (!event.getState().equals(EventStatus.PUBLISHED)) {
            throw new NotMetConditionsException("Event must have status PUBLISHED");
        }
        Comment comment = CommentMapper.toNewEntity(commentDto, event, user);

        return CommentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto updateComment(Long userId, Long eventId, Long commentId, NewCommentDto updateCommentDto) {
        existsUser(userId);
        existsEvent(eventId);
        Comment commentOld = existsComment(commentId);
        isOwnerComment(userId, commentOld.getUser().getId());
        if (!commentOld.getStatus().equals(CommentStatus.PENDING)) {
            throw new NotMetConditionsException("Only pending comments can be changed");
        }

        if (!commentOld.getText().equals(updateCommentDto.getText())) {
            commentOld.setText(updateCommentDto.getText());
        }
        return CommentMapper.toDto(commentRepository.save(commentOld));
    }

    @Override
    public void deleteComment(Long userId, Long eventId, Long commentId) {
        existsUser(userId);
        existsEvent(eventId);
        Comment comment = existsComment(commentId);
        isOwnerComment(userId, comment.getUser().getId());
        commentRepository.delete(comment);
    }

    @Override
    public List<CommentDto> getPendingComments() {
        List<Comment> comments = commentRepository.findAllByStatus(CommentStatus.PENDING);
        return comments.stream().map(CommentMapper::toDto).toList();
    }

    @Override
    public List<CommentDto> moderationComments(ModerationCommentStatusDto commentStatusDto) {
        if (!(commentStatusDto.getStatus().equals(CommentStatus.PUBLISHED) ||
                commentStatusDto.getStatus().equals(CommentStatus.REJECTED))) {
            throw new NotMetConditionsException("Update status must be PUBLISHED or REJECTED");
        }

        List<Comment> comments = commentRepository.findAllById(commentStatusDto.getComments());
        for (Comment comment : comments) {
            if (!comment.getStatus().equals(CommentStatus.PENDING)) {
                throw new NotMetConditionsException("Comment must have status PENDING");
            }
            comment.setStatus(commentStatusDto.getStatus());
        }
        commentRepository.saveAll(comments);
        return comments.stream().map(CommentMapper::toDto).toList();
    }

    @Override
    public List<CommentDto> getCommentsByEvent(Long eventId) {
        existsEvent(eventId);
        List<Comment> comments = commentRepository.findAllByEventIdAndStatusOrderByCreatedDesc(eventId,
                CommentStatus.PUBLISHED);
        return comments.stream().map(CommentMapper::toDto).toList();
    }

    @Override
    public void deleteCommentAdmin(Long commentId) {
        Comment comment = existsComment(commentId);
        commentRepository.delete(comment);
    }

    private Event existsEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Event with id=" + eventId + " was not found"));
    }

    private User existsUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User with id=" + userId + " was not found"));
    }

    private Comment existsComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Comment with id=" + commentId + " was not found"));
    }

    private void isOwnerComment(Long userId, Long initiatorId) {
        if (!Objects.equals(userId, initiatorId)) {
            throw new NotMetConditionsException("User with id=" + userId + " is not the creator of this comment");
        }
    }

}
