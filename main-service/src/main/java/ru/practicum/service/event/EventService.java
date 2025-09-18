package ru.practicum.service.event;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.dto.eventDto.*;
import ru.practicum.dto.requestDto.EventRequestStatusUpdateRequestDto;
import ru.practicum.dto.requestDto.EventRequestStatusUpdateResultDto;
import ru.practicum.dto.requestDto.RequestDto;
import ru.practicum.model.event.EventSort;
import ru.practicum.model.event.EventStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    List<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size);

    EventDto addEvent(Long userId, NewEventDto eventDto);

    EventDto getEventByUser(Long userId, Long eventId);

    EventDto updateEvent(Long userId, Long eventId, UpdateEventDto eventDto);

    List<RequestDto> getRequestsByUser(Long userId, Long eventId);

    EventRequestStatusUpdateResultDto updateRequestStatus(Long userId,
                                                          Long eventId,
                                                          EventRequestStatusUpdateRequestDto eventRequestStatusUpdate);

    List<EventShortDto> getEvents(String text,
                                  List<Long> categories, Boolean paid,
                                  LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                  Boolean onlyAvailable, EventSort sort, Integer from, Integer size, HttpServletRequest request);

    EventDto getEvent(Long eventId, HttpServletRequest request);

    EventDto updateEventAdmin(UpdateEventAdminDto updateEventDto, Long eventId);

    List<EventDto> getEventsAdmin(List<Long> users, List<EventStatus> state, List<Long> categories,
                                  LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);
}
