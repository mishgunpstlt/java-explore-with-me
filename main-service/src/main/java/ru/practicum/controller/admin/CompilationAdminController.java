package ru.practicum.controller.admin;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilationDto.CompilationDto;
import ru.practicum.dto.compilationDto.NewCompilationDto;
import ru.practicum.dto.compilationDto.UpdateCompilationDto;
import ru.practicum.service.compilation.CompilationService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
public class CompilationAdminController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@RequestBody @Valid NewCompilationDto compilationDto) {
        return compilationService.addCompilation(compilationDto);
    }

    @DeleteMapping(path = "/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long comId) {
        compilationService.deleteCompilation(comId);
    }

    @PatchMapping(path = "/{comId}")
    public CompilationDto updateCompilation(@PathVariable Long comId,
                                            @RequestBody @Valid UpdateCompilationDto compilationDto) {
        return compilationService.updateCompilation(comId, compilationDto);
    }
}
