package org.ignia.comprobante.productos;

import jakarta.transaction.Transactional;
import org.ignia.comprobante.categories.ICategoryService;
import org.ignia.comprobante.exception.ConflictException;
import org.ignia.comprobante.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final ICategoryService categoryService;

    public ProductService(ProductRepository productRepository, ICategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductMapper::toProductDTO)
                .toList();

    }

    @Override
    public Page<ProductDto> page(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(ProductMapper::toProductDTO);
    }

    @Override
    public Map<Long, Long> countsByCategory() {
        return productRepository.countsByCategory().stream()
                .collect(Collectors.toMap(CategoryProductCount::getCategoryId, CategoryProductCount::getCnt));
    }

    @Override
    public long countProducts() {
        return productRepository.count();
    }

    @Override
    public long countOutOfStock() {
        return productRepository.countByStock(0);
    }

    @Override
    public long countProductsByCategory(Long categoryId) {
        return productRepository.countByCategoria_Id(categoryId);
    }

    @Override
    public ProductDto getProductById(Long id) {
        return productRepository.findById(id)
                .map(ProductMapper::toProductDTO)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
    }

    @Override
    @Transactional
    public ProductDto saveProduct(ProductDto productDto) {
        String productUID = nextProductUID(productDto.getCategoryId());

        ProductModel product = ProductModel.builder()
                .productUID(productUID)
                .categoria(categoryService.getCategoryReference(productDto.getCategoryId()))
                .nameProduct(productDto.getNameProduct())
                .unitPrice(productDto.getUnitPrice())
                .stock(productDto.getStock())
                .profitPercentage(productDto.getProfitPercentage())
                .active(productDto.isActive())
                .build();

        return ProductMapper.toProductDTO(productRepository.save(product));
    }

    @Override
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        ProductModel productModel = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
        productModel.setNameProduct(productDto.getNameProduct());
        productModel.setUnitPrice(productDto.getUnitPrice());
        productModel.setStock(productDto.getStock());
        productModel.setProfitPercentage(productDto.getProfitPercentage());
        productModel.setActive(productDto.isActive());

        Long currentCatId = productModel.getCategoria() == null ? null : productModel.getCategoria().getId();
        if (productDto.getCategoryId() != null && !productDto.getCategoryId().equals(currentCatId)) {
            productModel.setProductUID(nextProductUID(productDto.getCategoryId()));
            productModel.setCategoria(categoryService.getCategoryReference(productDto.getCategoryId()));

        }
        return ProductMapper.toProductDTO(productRepository.save(productModel));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        ProductModel p = productRepository.findById(id).orElseThrow(()-> new NotFoundException("Producto no encontrado"));
        if (p.getItemSale() != null && !p.getItemSale().isEmpty()) throw new ConflictException("No se puede eliminar: tiene ventas asociadas");

        productRepository.deleteById(id);
    }

    private String nextProductUID(Long categoryId) {
        String prefix = categoryService.resolveCategoryUID(categoryId);
        int next = productRepository.findUidsByCategory(categoryId, prefix).stream()
                .map(uid -> uid.substring(uid.lastIndexOf('-')+1))
                .mapToInt(s -> {
                    try {
                        return Integer.parseInt(s);
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .max().orElse(0) +1;

        return prefix + "-" + String.format("%03d", next);
    }

    @Override
    public Page<ProductDto> page(String search, Long categoryId, Pageable pageable) {
        boolean hasSearch = search != null && !search.isBlank();
        if (!hasSearch && categoryId == null) return page(pageable);
        if (!hasSearch) return productRepository.findByCategoria_Id(categoryId, pageable).map(ProductMapper::toProductDTO);
        String q = search.trim();
        if (categoryId == null) return productRepository.findByNameProductContainingIgnoreCase(q, pageable).map(ProductMapper::toProductDTO);
        return productRepository.findByCategoria_IdAndNameProductContainingIgnoreCase(categoryId, q, pageable).map(ProductMapper::toProductDTO);
    }
}
