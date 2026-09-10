package org.ignia.comprobante.productos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IProductService {

    List<ProductDto> getAllProducts();

    Page<ProductDto> page(Pageable pageable);

    Map<Long, Long> countsByCategory();

    long countProducts();

    long countOutOfStock();

    long countProductsByCategory(Long categoryId);

    ProductDto getProductById(Long id);

    ProductDto saveProduct(ProductModel productModel);

    ProductDto updateProduct(Long id, ProductModel productModel);

    void deleteProduct(Long id);

}
