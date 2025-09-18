package ru.practicum.service.request;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.requestDto.RequestDto;
import ru.practicum.dto.requestDto.RequestMapper;
import ru.practicum.exception.NotMetConditionsException;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventStatus;
import ru.practicum.model.request.Request;
import ru.practicum.model.request.RequestStatus;
import ru.practicum.model.user.User;
import ru.practicum.repository.event.EventRepository;
import ru.practicum.repository.request.RequestRepository;
import ru.practicum.repository.user.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<RequestDto> getRequests(Long userId) {
        existsUser(userId);
        return requestRepository.findAllByRequesterId(userId)
                .stream()
                .map(RequestMapper::toDto)
                .toList();
    }

    @Override
    public RequestDto addRequest(Long userId, Long eventId) {
        User user = existsUser(userId);
        Event event = existsEvent(eventId);

        isOwnerEvent(userId, event.getInitiator().getId());

        if (Boolean.TRUE.equals(requestRepository.existsByRequesterIdAndEventId(userId, eventId))) {
            throw new NotMetConditionsException("could not execute statement; SQL [n/a];" +
                    " constraint [uq_request];" +
                    " nested exception is org.hibernate.exception.ConstraintViolationException:" +
                    " could not execute statement");
        }

        if (!event.getState().equals(EventStatus.PUBLISHED)) {
            throw new NotMetConditionsException("Нельзя участвовать в неопубликованном событии");
        }

        Integer confirmedCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (++confirmedCount >= event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            throw new NotMetConditionsException("Достигнут лимит запросов на участие");
        }

        Request request = RequestMapper.toNewEntity(event, user);

        log.info("Добавлена запрос: " + request);


        if (event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        return RequestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public RequestDto cancelRequest(Long userId, Long requestId) {
        User user = existsUser(userId);
        Request request = existsRequest(requestId);

        if (!Objects.equals(user.getId(), request.getRequester().getId())) {
            throw new NotMetConditionsException("User with id=" + userId + " is now the creator of this request");
        }

        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.toDto(requestRepository.save(request));
    }

    private Event existsEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Event with id=" + eventId + " was not found"));
    }

    private Request existsRequest(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Request with id=" + requestId + " was not found"));
    }

    private User existsUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User with id=" + userId + " was not found"));
    }

    private void isOwnerEvent(Long userId, Long initiatorId) {
        if (Objects.equals(userId, initiatorId)) {
            throw new NotMetConditionsException("User with id=" + userId + " is the creator of this event");
        }
    }
}
