package ru.practicum.dto.requestDto;

import ru.practicum.model.event.Event;
import ru.practicum.model.request.Request;
import ru.practicum.model.request.RequestStatus;
import ru.practicum.model.user.User;

import java.time.LocalDateTime;

public class RequestMapper {

    public static Request toNewEntity(Event event, User user) {
        return new Request(
                null,
                LocalDateTime.now(),
                event,
                user,
                RequestStatus.PENDING);
    }


    public static RequestDto toDto(Request request) {
        return new RequestDto(
                request.getCreated().withNano(request.getCreated().getNano() / 1_000_000 * 1_000_000),
                request.getEvent().getId(),
                request.getId(),
                request.getRequester().getId(),
                request.getStatus()
        );
    }
}
