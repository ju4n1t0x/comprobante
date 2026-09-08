package org.ignia.comprobante.productos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements IProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(Mapper::toPorudctDTO)
                .toList();
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
