package ru.practicum.controller.priv;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.commentDto.CommentDto;
import ru.practicum.dto.commentDto.NewCommentDto;
import ru.practicum.service.comment.CommentService;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "users/{userId}/events")
public class CommentPrivateController {

    private final CommentService commentService;

    @PostMapping(path = "/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable Long userId,
                                 @PathVariable Long eventId,
                                 @RequestBody @Valid NewCommentDto commentDto) {
        return commentService.addComment(userId, eventId, commentDto);
    }

    @PatchMapping(path = "/{eventId}/comments/{commentId}")
    public CommentDto updateComment(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @PathVariable Long commentId,
                                    @RequestBody @Valid NewCommentDto commentDto) {
        return commentService.updateComment(userId, eventId, commentId, commentDto);
    }

    @DeleteMapping(path = "/{eventId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long eventId,
                              @PathVariable Long commentId) {
        commentService.deleteComment(userId, eventId, commentId);
    }

}
