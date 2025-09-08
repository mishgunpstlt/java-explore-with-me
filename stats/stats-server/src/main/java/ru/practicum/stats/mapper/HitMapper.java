package ru.practicum.stats.mapper;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.stats.model.EndpointHit;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class HitMapper {

    public static EndpointHit toEntity(EndpointHitDto endpointHitDto) {
        return new EndpointHit(null, endpointHitDto.getApp(), endpointHitDto.getUri(),
                endpointHitDto.getIp(), toInstant(endpointHitDto.getTimestamp()));
    }

    public static EndpointHitDto toDto(EndpointHit endpointHit) {
        return new EndpointHitDto(endpointHit.getApp(), endpointHit.getUri(),
                endpointHit.getIp(), toLocalDateTime(endpointHit.getTimestamp()));
    }

    private static Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.UTC);
    }

    private static LocalDateTime toLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

}
