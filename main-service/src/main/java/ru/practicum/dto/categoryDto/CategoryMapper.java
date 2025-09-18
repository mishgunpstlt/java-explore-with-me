package ru.practicum.dto.categoryDto;

import ru.practicum.model.category.Category;

public class CategoryMapper {

    public static Category toNewEntity(NewCategoryDto categoryDto) {
        return new Category(null, categoryDto.getName());
    }

    public static CategoryDto toDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}
