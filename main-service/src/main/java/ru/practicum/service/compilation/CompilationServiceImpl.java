package ru.practicum.service.compilation;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.compilationDto.CompilationDto;
import ru.practicum.dto.compilationDto.CompilationMapper;
import ru.practicum.dto.compilationDto.NewCompilationDto;
import ru.practicum.dto.compilationDto.UpdateCompilationDto;
import ru.practicum.model.compilation.Compilation;
import ru.practicum.model.event.Event;
import ru.practicum.repository.compilation.CompilationRepository;
import ru.practicum.repository.event.EventRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;

    @Override
    public CompilationDto addCompilation(NewCompilationDto compilationDto) {

        List<Event> events = new ArrayList<>();

        if (compilationDto.getEvents() != null) {
            events = eventRepository.findAllById(compilationDto.getEvents());
        }

        Compilation compilation = CompilationMapper.toNewEntity(compilationDto, events);

        compilation = compilationRepository.save(compilation);

        return CompilationMapper.toDto(compilation);
    }

    @Override
    public void deleteCompilation(Long comId) {
        Compilation compilation = existsCompilation(comId);

        compilationRepository.delete(compilation);
    }

    @Override
    public CompilationDto updateCompilation(Long comId, UpdateCompilationDto compilationDto) {
        Compilation oldCompilation = existsCompilation(comId);

        if (compilationDto.getEvents() != null) {
            List<Event> events = eventRepository.findAllById(compilationDto.getEvents());
            oldCompilation.setEvents(events);
        }
        if (compilationDto.getPinned() != null) {
            oldCompilation.setPinned(compilationDto.getPinned());
        }
        if (compilationDto.getTitle() != null && !compilationDto.getTitle().isBlank()) {
            oldCompilation.setTitle(compilationDto.getTitle());
        }

        return CompilationMapper.toDto(compilationRepository.save(oldCompilation));
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        List<Compilation> compilations = compilationRepository
                .findAllByPinned(pinned, PageRequest.of(from / size, size)).getContent();

        return compilations.stream().map(CompilationMapper::toDto).toList();
    }

    @Override
    public CompilationDto getCompilationById(Long comId) {
        Compilation compilation = existsCompilation(comId);

        return CompilationMapper.toDto(compilation);
    }


    private Compilation existsCompilation(Long comId) {
        return compilationRepository.findById(comId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Category with id=" + comId + " was not found"));
    }
}
