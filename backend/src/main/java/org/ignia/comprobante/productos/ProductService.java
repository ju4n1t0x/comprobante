package org.ignia.comprobante.productos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(Mapper::toProductDTO)
                .toList();
    }

    @Override
    public Page<ProductDto> page(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(Mapper::toProductDTO);
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
        return null;
    }

    @Override
    public ProductDto saveProduct(ProductModel productModel) {
        return null;
    }

    @Override
    public ProductDto updateProduct(Long id, ProductModel productModel) {
        return null;
    }

    @Override
    public void deleteProduct(Long id) {
    }
}
