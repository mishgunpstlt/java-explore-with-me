package ru.practicum.service.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.dto.eventDto.*;
import ru.practicum.dto.requestDto.EventRequestStatusUpdateRequestDto;
import ru.practicum.dto.requestDto.EventRequestStatusUpdateResultDto;
import ru.practicum.dto.requestDto.RequestDto;
import ru.practicum.dto.requestDto.RequestMapper;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.NotMetConditionsException;
import ru.practicum.model.category.Category;
import ru.practicum.model.event.*;
import ru.practicum.model.request.Request;
import ru.practicum.model.request.RequestStatus;
import ru.practicum.model.user.User;
import ru.practicum.repository.category.CategoryRepository;
import ru.practicum.repository.event.EventRepository;
import ru.practicum.repository.request.RequestRepository;
import ru.practicum.repository.user.UserRepository;
import ru.practicum.stats.StatsClient;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    @Override
    public List<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size) {
        existsUser(userId);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, PageRequest.of(from / size, size));

        List<EventShortDto> eventDtos = events.stream().map(EventMapper::toEventShortDto).toList();
        enrichViewsEvents(eventDtos);
        enrichConfirmedRequestEvents(eventDtos);

        return eventDtos;
    }

    @Override
    public EventDto addEvent(Long userId, NewEventDto eventDto) {
        correctTimeCreated(eventDto.getEventDate());

        User initiator = existsUser(userId);
        Category category = existsCategory(eventDto.getCategory());

        Event event = EventMapper.toEntity(eventDto, category, initiator);


        return EventMapper.toDto(eventRepository.save(event));
    }

    @Override
    public EventDto getEventByUser(Long userId, Long eventId) {
        existsUser(userId);

        Event event = existsEvent(eventId);

        isOwnerEvent(userId, event.getInitiator().getId());

        EventDto eventDto = EventMapper.toDto(event);
        enrichViewsEvent(eventDto);
        enrichConfirmedRequestEvent(eventDto);

        return eventDto;
    }

    @Override
    public EventDto updateEvent(Long userId, Long eventId, UpdateEventDto updateEventDto) {
        existsUser(userId);

        Event eventOld = existsEvent(eventId);
        isOwnerEvent(userId, eventOld.getInitiator().getId());

        if (!eventOld.getState().equals(EventStatus.PENDING) && !eventOld.getState().equals(EventStatus.REJECTED)) {
            throw new NotMetConditionsException("Only pending or rejected events can be changed");
        }

        updateEventBase(eventOld, updateEventDto);

        if (updateEventDto.getStateAction() != null) {
            if (updateEventDto.getStateAction().equals(ActionStatus.CANCEL_REVIEW)) {
                eventOld.setState(EventStatus.CANCELED);
            } else if (updateEventDto.getStateAction().equals(ActionStatus.SEND_TO_REVIEW)) {
                eventOld.setState(EventStatus.PENDING);
            }
        }

        EventDto event = EventMapper.toDto(eventRepository.save(eventOld));

        enrichViewsEvent(event);
        enrichConfirmedRequestEvent(event);

        return event;
    }

    @Override
    public List<RequestDto> getRequestsByUser(Long userId, Long eventId) {
        existsUser(userId);
        Event event = existsEvent(eventId);
        isOwnerEvent(userId, event.getInitiator().getId());

        List<Request> requests = requestRepository.findAllByEventId(eventId);

        return requests.stream().map(RequestMapper::toDto).toList();
    }

    @Override
    public EventRequestStatusUpdateResultDto updateRequestStatus(Long userId,
                                                                 Long eventId,
                                                                 EventRequestStatusUpdateRequestDto eventRequestStatusUpdate) {
        existsUser(userId);
        Event event = existsEvent(eventId);
        isOwnerEvent(userId, event.getInitiator().getId());

        List<RequestDto> confirmedRequests = new ArrayList<>();
        List<RequestDto> rejectedRequests = new ArrayList<>();

        List<Long> requestIds = eventRequestStatusUpdate.getRequestIds();

        List<Request> requests = requestRepository.findAllByIdIn(requestIds);

        if (Objects.equals(event.getParticipantLimit(), 0)
                || Boolean.FALSE.equals(event.getRequestModeration())) {
            for (Request request : requests) {
                request.setStatus(RequestStatus.CONFIRMED);
                confirmedRequests.add(RequestMapper.toDto(request));
            }
            requestRepository.saveAll(requests);
            return new EventRequestStatusUpdateResultDto(confirmedRequests, rejectedRequests);
        }

        Integer confirmedCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        Integer participantLimit = event.getParticipantLimit();

        if (Objects.equals(event.getParticipantLimit(), confirmedCount)) {
            throw new NotMetConditionsException("The participant limit has been reached");
        }

        for (Request request : requests) {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new NotMetConditionsException("Request must have status PENDING");
            }
            if (eventRequestStatusUpdate.getStatus().equals(RequestStatus.CONFIRMED)) {
                if (confirmedCount >= participantLimit) {
                    throw new NotMetConditionsException("The participant limit has been reached");
                }
                request.setStatus(RequestStatus.CONFIRMED);
                confirmedRequests.add(RequestMapper.toDto(request));
                confirmedCount++;
            }
            if (eventRequestStatusUpdate.getStatus().equals(RequestStatus.REJECTED)) {
                if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
                    throw new NotMetConditionsException("Нельзя отменить уже принятую заявку на участие в событии");
                }
                request.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(RequestMapper.toDto(request));
            }
        }
        requestRepository.saveAll(requests);

        if (confirmedCount >= event.getParticipantLimit()) {
            List<Request> pendingRequests = requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.PENDING);
            for (Request pending : pendingRequests) {
                pending.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(RequestMapper.toDto(pending));
            }
            requestRepository.saveAll(pendingRequests);
        }

        return new EventRequestStatusUpdateResultDto(confirmedRequests, rejectedRequests);
    }

    @Override
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                         Boolean onlyAvailable, EventSort sort, Integer from, Integer size,
                                         HttpServletRequest request) {

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("Дата начала не может быть позже даты конца поиска");
        }

        Pageable pageable = PageRequest.of(from / size, size);

        Page<Event> page;


        if (rangeStart == null || rangeEnd == null) {
            page = eventRepository.searchEventCurrentTime(text, categories, paid, pageable);
        } else {
            page = eventRepository.searchEvent(text, categories, rangeStart, rangeEnd, paid, pageable);
        }

        List<Event> events = page.getContent();

        List<Long> eventIds = events.stream().map(Event::getId).toList();
        List<Object[]> counts = requestRepository.countConfirmedRequestsByEventIds(eventIds);

        Map<Long, Long> confirmedMap = counts.stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],
                        arr -> (Long) arr[1]
                ));

        if (Boolean.TRUE.equals(onlyAvailable)) {
            events.removeIf(event -> {
                Long confirmedCount = confirmedMap.getOrDefault(event.getId(), 0L);
                return confirmedCount >= event.getParticipantLimit() && event.getParticipantLimit() != 0;
            });
        }

        List<EventShortDto> eventShortDtos = new ArrayList<>(events.stream()
                .map(EventMapper::toEventShortDto).toList());

        if (sort == null || sort.equals(EventSort.EVENT_DATE)) {
            eventShortDtos.sort(Comparator.comparing(EventShortDto::getEventDate));
        } else if (sort.equals(EventSort.VIEWS)) {
            eventShortDtos.sort(Comparator.comparing(EventShortDto::getViews).reversed());
        }

        EndpointHitDto hitDto = new EndpointHitDto(
                "ewm-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                Instant.now()
        );
        statsClient.addHit(hitDto);

        for (EventShortDto event : eventShortDtos) {
            event.setConfirmedRequests(confirmedMap.getOrDefault(event.getId(), 0L).intValue());
        }
        enrichViewsEvents(eventShortDtos);

        return eventShortDtos;
    }

    @Override
    public EventDto getEvent(Long eventId, HttpServletRequest request) {
        Event event = existsEvent(eventId);

        if (!event.getState().equals(EventStatus.PUBLISHED)) {
            throw new NotFoundException("Нельзя получить информацию о событии" +
                    " по публичному эндпоинту без публикации");
        }

        EndpointHitDto hitDto = new EndpointHitDto(
                "ewm-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                Instant.now()
        );
        statsClient.addHit(hitDto);
        log.info("Время LocalDateTime хита =" + hitDto.getTimestamp());

        EventDto eventDto = EventMapper.toDto(event);

        enrichConfirmedRequestEvent(eventDto);
        enrichViewsEvent(eventDto);

        return eventDto;
    }

    @Override
    public EventDto updateEventAdmin(UpdateEventAdminDto updateEventDto, Long eventId) {
        Event eventOld = existsEvent(eventId);

        updateEventBase(eventOld, updateEventDto);

        if (updateEventDto.getStateAction() != null) {
            if (updateEventDto.getStateAction().equals(AdminActionStatus.PUBLISH_EVENT)) {
                if (!eventOld.getState().equals(EventStatus.PENDING)) {
                    throw new NotMetConditionsException(
                            "Cannot publish the event because it's not in the right state: " + eventOld.getState()
                    );
                }
                if (eventOld.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                    throw new NotMetConditionsException(
                            "Event date must be at least 1 hour after the publication time"
                    );
                }
                eventOld.setState(EventStatus.PUBLISHED);
                eventOld.setPublishedOn(LocalDateTime.now());

            } else if (updateEventDto.getStateAction().equals(AdminActionStatus.REJECT_EVENT)) {
                if (eventOld.getState().equals(EventStatus.PUBLISHED)) {
                    throw new NotMetConditionsException(
                            "Cannot reject the event because it is already published"
                    );
                }
                eventOld.setState(EventStatus.REJECTED);
            }
        }

        EventDto event = EventMapper.toDto(eventRepository.save(eventOld));

        enrichConfirmedRequestEvent(event);
        enrichViewsEvent(event);

        return event;
    }

    @Override
    public List<EventDto> getEventsAdmin(List<Long> users, List<EventStatus> state, List<Long> categories,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("Дата начала не может быть позже даты конца поиска");
        }

        Pageable pageable = PageRequest.of(from / size, size);


        Page<Event> page = eventRepository.searchEventAdmin(users, state, categories,
                rangeStart, rangeEnd, pageable);

        List<EventDto> events = page.getContent().stream().map(EventMapper::toDto).toList();

        enrichConfirmedRequestEvents(events);
        enrichViewsEvents(events);

        return events;
    }

    private <T extends EventViewable> void enrichConfirmedRequestEvents(List<T> events) {

        List<Long> eventIds = events.stream()
                .map(EventViewable::getId)
                .toList();

        List<Object[]> counts = requestRepository.countConfirmedRequestsByEventIds(eventIds);
        Map<Long, Long> confirmedMap = counts.stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],
                        arr -> (Long) arr[1]
                ));

        for (T event : events) {
            event.setConfirmedRequests(confirmedMap.getOrDefault(event.getId(), 0L).intValue());
        }

    }

    private <T extends EventViewable> void enrichViewsEvents(List<T> events) {
        List<Long> eventIds = events.stream()
                .map(EventViewable::getId)
                .toList();

        List<String> uris = eventIds.stream()
                .map(id -> "/events/" + id)
                .toList();

        Instant start = Instant.parse("2000-01-01T00:00:00Z");
        Instant end = Instant.now().plusSeconds(1);

        ResponseEntity<Object> response = statsClient.getStats(
                start,
                end,
                uris,
                true
        );

        Object body = response.getBody();

        List<ViewStatsDto> stats = new ArrayList<>();

        if (body instanceof List<?> list) {
            ObjectMapper mapper = new ObjectMapper();
            stats = list.stream()
                    .map(item -> mapper.convertValue(item, ViewStatsDto.class))
                    .toList();
        }

        Map<Long, Integer> viewsMap = stats.stream()
                .collect(Collectors.toMap(
                        dto -> Long.parseLong(dto.getUri().replace("/events/", "")),
                        ViewStatsDto::getHits
                ));
        for (T event : events) {
            event.setViews(viewsMap.getOrDefault(event.getId(), 0));
        }
    }

    private void enrichConfirmedRequestEvent(EventDto event) {
        enrichConfirmedRequestEvents(List.of(event));
    }

    private void enrichViewsEvent(EventDto event) {
        enrichViewsEvents(List.of(event));
    }

    private void updateEventBase(Event event, UpdateEventBaseDto updateDto) {
        if (updateDto.getEventDate() != null) {
            correctTimeCreated(updateDto.getEventDate());
            event.setEventDate(updateDto.getEventDate());
        }
        if (updateDto.getAnnotation() != null && !updateDto.getAnnotation().isBlank()) {
            event.setAnnotation(updateDto.getAnnotation());
        }
        if (updateDto.getCategory() != null) {
            event.setCategory(categoryRepository.findById(updateDto.getCategory())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Category with id=" + updateDto.getCategory() + " was not found")));
        }
        if (updateDto.getDescription() != null && !updateDto.getDescription().isBlank()) {
            event.setDescription(updateDto.getDescription());
        }
        if (updateDto.getLocation() != null) {
            event.setLocation(updateDto.getLocation());
        }
        if (updateDto.getPaid()) {
            event.setPaid(updateDto.getPaid());
        }
        if (updateDto.getParticipantLimit() != 0) {
            event.setParticipantLimit(updateDto.getParticipantLimit());
        }
        if (!updateDto.getRequestModeration()) {
            event.setRequestModeration(updateDto.getRequestModeration());
        }
        if (updateDto.getTitle() != null && !updateDto.getTitle().isBlank()) {
            event.setTitle(updateDto.getTitle());
        }
    }

    private void isOwnerEvent(Long userId, Long initiatorId) {
        if (!Objects.equals(userId, initiatorId)) {
            throw new NotMetConditionsException("User with id=" + userId + " is not the creator of this event");
        }
    }

    private void correctTimeCreated(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Field: eventDate. Error: должно содержать дату, " +
                    "которая еще не наступила. Value: " + eventDate);
        }
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

    private Category existsCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Category with id=" + categoryId + " was not found"));
    }
}
