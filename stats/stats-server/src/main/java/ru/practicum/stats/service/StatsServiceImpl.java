package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stats.dal.StatsRepository;
import ru.practicum.stats.mapper.HitMapper;
import ru.practicum.stats.model.EndpointHit;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public void addHit(EndpointHitDto hitDto) {
        EndpointHit endpointHit = HitMapper.toEntity(hitDto);
        endpointHit = statsRepository.save(endpointHit);
        log.info("Сохранение статистики запроса пользователя: {}", endpointHit);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime startTime, LocalDateTime endTime,
                                       List<String> uris, boolean unique) {

        Instant start = startTime.toInstant(ZoneOffset.UTC);
        Instant end = endTime.toInstant(ZoneOffset.UTC);

        List<Object[]> stats;

        if (unique) {
            stats = statsRepository.findUniqueStats(start, end, uris);
        } else {
            stats = statsRepository.findAllStats(start, end, uris);
        }

        log.info("Получения статистики: {}", stats);

        return stats.stream()
                .map(obj -> new ViewStatsDto(obj[0].toString(),
                        obj[1].toString(), ((Number) obj[2]).intValue()))
                .toList();
    }
}
