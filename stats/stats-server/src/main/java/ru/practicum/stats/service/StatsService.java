package ru.practicum.stats.service;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.Instant;
import java.util.List;

public interface StatsService {

    void addHit(EndpointHitDto hitDto);

    List<ViewStatsDto> getStats(Instant start, Instant end, List<String> uris, boolean unique);
}
