package ru.practicum.stats;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final StatsClient statsClient;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    private void addHit(@RequestBody @Valid EndpointHitDto hitDto) {
        statsClient.addHit(hitDto);
    }

    @GetMapping("/stats")
    private ResponseEntity<Object> getStats(@RequestParam String start,
                                            @RequestParam String end,
                                            @RequestParam(required = false) List<String> uris,
                                            @RequestParam(defaultValue = "false") boolean unique) {
        return statsClient.getStats(start, end, uris, unique);
    }
}
