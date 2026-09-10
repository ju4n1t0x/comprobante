package org.ignia.comprobante.categories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICategoryService {

    List<CategoryDto> getAllCategories();

    long countCategories();

    Page<CategoryDto> page(Long categoryFilter, Pageable pageable);

    CategoryDto getCategoryById(Long id);

    CategoryDto saveCategory(CategoryDto categoryDto);

    CategoryDto updateCategory(Long id, CategoryDto categoryDto);

    void deleteCategory(Long id);
}
