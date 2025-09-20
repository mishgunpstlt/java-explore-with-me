package ru.practicum.controller.priv;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.eventDto.EventDto;
import ru.practicum.dto.eventDto.EventShortDto;
import ru.practicum.dto.eventDto.NewEventDto;
import ru.practicum.dto.eventDto.UpdateEventDto;
import ru.practicum.dto.requestDto.EventRequestStatusUpdateRequestDto;
import ru.practicum.dto.requestDto.EventRequestStatusUpdateResultDto;
import ru.practicum.dto.requestDto.RequestDto;
import ru.practicum.service.event.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "users/{userId}/events")
public class EventPrivateController {

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEventsByUser(@PathVariable Long userId,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getEventsByUser(userId, from, size);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto addEvent(@PathVariable Long userId, @RequestBody @Valid NewEventDto eventDto) {
        return eventService.addEvent(userId, eventDto);
    }

    @GetMapping(path = "/{eventId}")
    public EventDto getEventByUser(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getEventByUser(userId, eventId);
    }

    @PatchMapping(path = "/{eventId}")
    public EventDto updateEvent(@PathVariable Long userId,
                                @PathVariable Long eventId,
                                @RequestBody @Valid UpdateEventDto eventDto) {
        return eventService.updateEvent(userId, eventId, eventDto);
    }

    @GetMapping(path = "/{eventId}/requests")
    public List<RequestDto> getRequestsByUser(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getRequestsByUser(userId, eventId);
    }

    @PatchMapping(path = "/{eventId}/requests")
    public EventRequestStatusUpdateResultDto updateRequestStatus(@PathVariable Long userId,
                                                                 @PathVariable Long eventId,
                                                                 @RequestBody EventRequestStatusUpdateRequestDto eventRequestStatusUpdate) {
        return eventService.updateRequestStatus(userId, eventId, eventRequestStatusUpdate);
    }
}
