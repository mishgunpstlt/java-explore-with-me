package ru.practicum.dto.eventDto;

import ru.practicum.dto.categoryDto.CategoryDto;
import ru.practicum.dto.userDto.UserShortDto;
import ru.practicum.model.category.Category;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventStatus;
import ru.practicum.model.user.User;

import java.time.LocalDateTime;

public class EventMapper {

    public static Event toEntity(NewEventDto dto, Category category, User initiator) {
        return new Event(
                null,
                dto.getAnnotation(),
                category,
                LocalDateTime.now(),
                dto.getDescription(),
                dto.getEventDate(),
                initiator,
                dto.getLocation(),
                dto.getPaid(),
                dto.getParticipantLimit(),
                null,
                dto.getRequestModeration(),
                EventStatus.PENDING,
                dto.getTitle()
        );
    }

    public static EventShortDto toEventShortDto(Event event) {
        UserShortDto initiatorDto = new UserShortDto(
                event.getInitiator().getId(),
                event.getInitiator().getName()
        );

        CategoryDto categoryDto = new CategoryDto(
                event.getCategory().getId(),
                event.getCategory().getName()
        );

        return new EventShortDto(
                event.getAnnotation(),
                categoryDto,
                null,
                event.getEventDate(),
                event.getId(),
                initiatorDto,
                event.getPaid(),
                event.getTitle(),
                null);
    }

    public static EventDto toDto(Event event) {

        UserShortDto initiatorDto = new UserShortDto(
                event.getInitiator().getId(),
                event.getInitiator().getName()
        );

        CategoryDto categoryDto = new CategoryDto(
                event.getCategory().getId(),
                event.getCategory().getName()
        );

        return new EventDto(
                event.getAnnotation(),
                categoryDto,
                null,
                truncateToMillis(event.getCreatedOn()),
                event.getDescription(),
                event.getEventDate(),
                event.getId(),
                initiatorDto,
                event.getLocation(),
                event.getPaid(),
                event.getParticipantLimit(),
                truncateToMillis(event.getPublishedOn()),
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                null
        );
    }

    private static LocalDateTime truncateToMillis(LocalDateTime time) {
        if (time == null) return null;
        return time.withNano(time.getNano() / 1_000_000 * 1_000_000);
    }

}
