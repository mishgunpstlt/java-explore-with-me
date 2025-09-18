package ru.practicum.dto.eventDto;

public interface EventViewable {
    Long getId();

    void setViews(Integer views);

    void setConfirmedRequests(Integer confirmedRequests);
}
