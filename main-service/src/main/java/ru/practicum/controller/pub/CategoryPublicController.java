package ru.practicum.controller.pub;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.categoryDto.CategoryDto;
import ru.practicum.service.category.CategorySevice;

import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
public class CategoryPublicController {

    private final CategorySevice categorySevice;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") Integer from,
                                           @RequestParam(defaultValue = "10") Integer size) {
        return categorySevice.getCategories(from, size);
    }

    @GetMapping(path = "/{catId}")
    public CategoryDto getCategoryById(@PathVariable Long catId) {
        return categorySevice.getCatById(catId);
    }
}
