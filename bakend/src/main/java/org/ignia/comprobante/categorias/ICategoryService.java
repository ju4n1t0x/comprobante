package org.ignia.comprobante.categorias;

import java.util.List;

public interface ICategoryService {

    List<CategoryDto> getAllCategories();

    CategoryDto getCategoryById(Long id);

    CategoryDto saveCategory(CategoryDto categoryDto);

    CategoryDto updateCategory(Long id, CategoryDto categoryDto);

    void deleteCategory(Long id);
}
