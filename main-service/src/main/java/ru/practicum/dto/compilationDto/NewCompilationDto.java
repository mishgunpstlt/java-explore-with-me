package ru.practicum.dto.compilationDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class NewCompilationDto {

    private List<Long> events;

    private Boolean pinned = false;

    @NotBlank
    @Size(max = 50)
    private String title;

}
