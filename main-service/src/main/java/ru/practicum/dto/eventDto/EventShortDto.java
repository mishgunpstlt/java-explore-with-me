package ru.practicum.dto.eventDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.dto.categoryDto.CategoryDto;
import ru.practicum.dto.userDto.UserShortDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class EventShortDto implements EventViewable {

    private String annotation;

    private CategoryDto category;

    private Integer confirmedRequests;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Long id;

    private UserShortDto initiator;

    private Boolean paid;

    private String title;

    private Integer views;
}
