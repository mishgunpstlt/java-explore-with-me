package ru.practicum.dto.commentDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.model.comment.CommentStatus;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModerationCommentStatusDto {

    private List<Long> comments;

    private CommentStatus status;

}
