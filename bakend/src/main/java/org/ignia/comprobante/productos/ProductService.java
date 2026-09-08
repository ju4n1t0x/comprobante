package org.ignia.comprobante.productos;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements IProductService {
    @Override
    public List<ProductDto> getAllProducts() {
        return List.of();
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
