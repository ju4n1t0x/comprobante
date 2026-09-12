package org.ignia.comprobante.categories;

import org.ignia.comprobante.exception.ConflictException;
import org.ignia.comprobante.exception.NotFoundException;
import org.ignia.comprobante.productos.CategoryProductCount;
import org.ignia.comprobante.productos.IProductService;
import org.ignia.comprobante.productos.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .toList();
    }

    @Override
    public long countCategories() {
        return categoryRepository.count();
    }

    @Override
    public Page<CategoryDto> page(Long categoryFilter, Pageable pageable) {
        Map<Long, Long> counts = productRepository.countsByCategory().stream()
                .collect(Collectors.toMap(CategoryProductCount::getCategoryId, CategoryProductCount::getCnt));

        if (categoryFilter != null) {
            CategoryModel model = categoryRepository.findById(categoryFilter).orElse(null);
            List<CategoryDto> list = model == null
                    ? List.of()
                    : List.of(CategoryMapper.toCategoryDto(model, counts.getOrDefault(model.getId(), 0L)));
            return new PageImpl<>(list, pageable, list.size());
        }

        return categoryRepository.findAll(pageable)
                .map(m -> CategoryMapper.toCategoryDto(m, counts.getOrDefault(m.getId(), 0L)));
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
        String categoryUID = categoryDto.getName()
                .trim()
                .substring(0, Math.min(3, categoryDto.getName().trim().length()))
                .toUpperCase();

        CategoryModel category = CategoryModel.builder()
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
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada"));

        categoryModel.setName(categoryDto.getName());
        categoryModel.setDescription(categoryDto.getDescription());

        return CategoryMapper.toCategoryDto(categoryRepository.save(categoryModel));
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Categoría no encontrada");
        }
        if (productRepository.countByCategoria_Id(id) > 0) {
            throw new ConflictException("No se puede eliminar: la categoría tiene productos asociados");
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public String resolveCategoryUID(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .map(CategoryModel::getCategoryUID)
                .orElseThrow(() -> new NotFoundException("Categoria no encontrada"));
    }

    @Override
    public CategoryModel getCategoryReference(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Categoria no encontrada"));
    }
}
