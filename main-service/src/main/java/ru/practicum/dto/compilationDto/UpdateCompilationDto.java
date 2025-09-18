package ru.practicum.dto.compilationDto;


import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class UpdateCompilationDto {

    private List<Long> events;

    private Boolean pinned = false;

    @Size(max = 50)
    private String title;


}
