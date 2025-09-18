package ru.practicum.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.model.request.RequestStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RequestDto {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime created;

    private Long event;

    private Long id;

    private Long requester;

    private RequestStatus status;

}
