package ru.practicum.stats.mapper;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.stats.model.EndpointHit;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class HitMapper {

    public static EndpointHit toEntity(EndpointHitDto endpointHitDto) {
        return new EndpointHit(null, endpointHitDto.getApp(), endpointHitDto.getUri(),
                endpointHitDto.getIp(), endpointHitDto.getTimestamp());
    }

    public static EndpointHitDto toDto(EndpointHit endpointHit) {
        return new EndpointHitDto(endpointHit.getApp(), endpointHit.getUri(),
                endpointHit.getIp(), endpointHit.getTimestamp());
    }

    private static Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneOffset.UTC).toInstant();
    }

    private static LocalDateTime toLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

}
