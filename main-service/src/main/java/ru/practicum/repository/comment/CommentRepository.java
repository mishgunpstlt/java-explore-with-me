package ru.practicum.repository.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.comment.Comment;
import ru.practicum.model.comment.CommentStatus;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByStatus(CommentStatus status);

    List<Comment> findAllByEventIdAndStatusOrderByCreatedDesc(Long eventId, CommentStatus status);
}
