package ru.practicum.service.category;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.categoryDto.CategoryDto;
import ru.practicum.dto.categoryDto.CategoryMapper;
import ru.practicum.dto.categoryDto.NewCategoryDto;
import ru.practicum.exception.AlreadyExistsException;
import ru.practicum.exception.NotMetConditionsException;
import ru.practicum.model.category.Category;
import ru.practicum.repository.category.CategoryRepository;
import ru.practicum.repository.event.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategorySevice {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto addCategory(NewCategoryDto categoryDto) {
        existsCategoryByName(categoryDto.getName());
        Category category = categoryRepository.save(CategoryMapper.toNewEntity(categoryDto));
        log.info("Добавлена категория: " + category);
        return CategoryMapper.toDto(category);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Category category = getCategoryById(categoryId);

        boolean hasEvents = eventRepository.existsByCategoryId(categoryId);
        if (hasEvents) {
            throw new NotMetConditionsException("The category is not empty");
        }

        categoryRepository.delete(category);
    }

    @Override
    public CategoryDto updateCategory(Long categoryId, NewCategoryDto categoryDto) {
        Category category = getCategoryById(categoryId);
        if (!category.getName().equals(categoryDto.getName())) {
            existsCategoryByName(categoryDto.getName());
            category.setName(categoryDto.getName());
        }
        return CategoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        List<Category> categories = categoryRepository.findAll(PageRequest.of(from / size, size)).toList();
        return categories.stream().map(CategoryMapper::toDto).toList();
    }

    @Override
    public CategoryDto getCatById(Long catId) {
        return CategoryMapper.toDto(getCategoryById(catId));
    }

    private Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Category with id=" + categoryId + " was not found"));
    }

    private void existsCategoryByName(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new AlreadyExistsException("could not execute statement; SQL [n/a]; " +
                    "constraint [uq_category_name]; " +
                    "nested exception is org.hibernate.exception.ConstraintViolationException: " +
                    "could not execute statement");
        }
    }
}
