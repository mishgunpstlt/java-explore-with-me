package ru.practicum.service.request;


import ru.practicum.dto.requestDto.RequestDto;

import java.util.List;

public interface RequestService {

    List<RequestDto> getRequests(Long userId);

    RequestDto addRequest(Long userId, Long eventId);

    RequestDto cancelRequest(Long userId, Long requestId);
}
