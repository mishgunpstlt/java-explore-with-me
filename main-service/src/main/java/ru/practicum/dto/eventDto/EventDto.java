package ru.practicum.dto.eventDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.dto.categoryDto.CategoryDto;
import ru.practicum.dto.userDto.UserShortDto;
import ru.practicum.model.event.EventStatus;
import ru.practicum.model.event.Location;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class EventDto implements EventViewable {

    private String annotation;

    private CategoryDto category;

    private Integer confirmedRequests;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Long id;

    private UserShortDto initiator;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    private EventStatus state;

    private String title;

    private Integer views;
}
