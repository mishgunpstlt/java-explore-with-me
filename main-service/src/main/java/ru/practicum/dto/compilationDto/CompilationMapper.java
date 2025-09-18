package ru.practicum.dto.compilationDto;

import ru.practicum.dto.eventDto.EventMapper;
import ru.practicum.dto.eventDto.EventShortDto;
import ru.practicum.model.compilation.Compilation;
import ru.practicum.model.event.Event;

import java.util.List;

public class CompilationMapper {

    public static Compilation toNewEntity(NewCompilationDto compilationDto, List<Event> events) {
        return new Compilation(null, events,
                compilationDto.getPinned(), compilationDto.getTitle());
    }

    public static CompilationDto toDto(Compilation compilation) {
        List<EventShortDto> eventShortDtos = compilation.getEvents().stream()
                .map(EventMapper::toEventShortDto)
                .toList();

        return new CompilationDto(eventShortDtos, compilation.getId(),
                compilation.getPinned(), compilation.getTitle());
    }

}
