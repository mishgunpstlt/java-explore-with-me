package ru.practicum.controller.admin;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.commentDto.CommentDto;
import ru.practicum.dto.commentDto.ModerationCommentStatusDto;
import ru.practicum.service.comment.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/comments")
public class CommentAdminController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getPendingComments() {
        return commentService.getPendingComments();
    }

    @PatchMapping()
    public List<CommentDto> moderationComments(@RequestBody ModerationCommentStatusDto commentStatusDto) {
        return commentService.moderationComments(commentStatusDto);
    }

    @DeleteMapping(path = "/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentAdmin(@PathVariable Long commentId) {
        commentService.deleteCommentAdmin(commentId);
    }

}
