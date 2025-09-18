package ru.practicum.service.category;

import ru.practicum.dto.categoryDto.CategoryDto;
import ru.practicum.dto.categoryDto.NewCategoryDto;

import java.util.List;

public interface CategorySevice {

    CategoryDto addCategory(NewCategoryDto categoryDto);

    void deleteCategory(Long categoryId);

    CategoryDto updateCategory(Long categoryId, NewCategoryDto categoryDto);

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCatById(Long catId);
}
