package ru.practicum.controller.admin;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.categoryDto.CategoryDto;
import ru.practicum.dto.categoryDto.NewCategoryDto;
import ru.practicum.service.category.CategorySevice;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {

    private final CategorySevice categorySevice;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody @Valid NewCategoryDto categoryDto) {
        return categorySevice.addCategory(categoryDto);
    }

    @DeleteMapping(path = "/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        categorySevice.deleteCategory(catId);
    }

    @PatchMapping(path = "/{catId}")
    public CategoryDto updateCategory(@PathVariable Long catId, @RequestBody @Valid NewCategoryDto categoryDto) {
        return categorySevice.updateCategory(catId, categoryDto);
    }
}
