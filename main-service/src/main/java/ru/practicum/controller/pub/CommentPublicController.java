package ru.practicum.controller.pub;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.commentDto.CommentDto;
import ru.practicum.service.comment.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events/{eventId}/comments")
public class CommentPublicController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getCommentsByEvent(@PathVariable Long eventId) {
        return commentService.getCommentsByEvent(eventId);
    }
}
