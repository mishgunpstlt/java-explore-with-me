package ru.practicum.stats;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stats.service.StatsService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    private void addHit(@RequestBody @Valid EndpointHitDto hitDto) {
        statsService.addHit(hitDto);
    }

    @GetMapping("/stats")
    private List<ViewStatsDto> getStats(@RequestParam String start,
                                        @RequestParam String end,
                                        @RequestParam(required = false) List<String> uris,
                                        @RequestParam(defaultValue = "false") boolean unique) {
        Instant startInstant = parseDateTime(start).toInstant(ZoneOffset.UTC);
        Instant endInstant = parseDateTime(end).toInstant(ZoneOffset.UTC);
        return statsService.getStats(startInstant, endInstant, uris, unique);
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        String decoded = URLDecoder.decode(dateTimeStr, StandardCharsets.UTF_8);

        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatter2 = DateTimeFormatter.ISO_DATE_TIME;

        try {
            return LocalDateTime.parse(decoded, formatter1);
        } catch (DateTimeParseException e) {
            return LocalDateTime.parse(decoded, formatter2);
        }
    }
}
