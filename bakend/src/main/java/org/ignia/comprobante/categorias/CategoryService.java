package org.ignia.comprobante.categorias;

import org.ignia.comprobante.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService implements ICategoryService{

    @Autowired
    private CategoryRepository categoryRepository;

    public List<CategoryDto> getAllCategories(){
        return categoryRepository.findAll()
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .findFirst()
                .orElse(null);
    }

    @Override
    public CategoryDto saveCategory(CategoryDto categoryDto) {

        //creamos el UID a partir de las primeras 3 letras del nombre de la cateogria
        String categoryUID = categoryDto.getName()
                .trim()
                .substring(0, Math.min(3, categoryDto.getName().trim().length()))
                .toUpperCase();

        var category = CategoryModel.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .categoryUID(categoryUID)
                .description(categoryDto.getDescription())
                .build();

        return CategoryMapper.toCategoryDto(categoryRepository.save(category));

    }

    @Override
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {

        CategoryModel categoryModel = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));

        categoryModel.setName(categoryDto.getName());
        categoryModel.setDescription(categoryDto.getDescription());

        return CategoryMapper.toCategoryDto(categoryRepository.save(categoryModel));
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)){
            throw new NotFoundException(("Producto no encontrado"));
        }
        categoryRepository.deleteById(id);
    }
}
