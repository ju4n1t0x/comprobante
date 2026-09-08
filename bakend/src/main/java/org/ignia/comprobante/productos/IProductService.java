package org.ignia.comprobante.productos;

import java.util.List;

public interface IProductService {

    List<ProductDto> getAllProducts();

    ProductDto getProductById(Long id);

    ProductDto saveProduct(ProductModel productModel);

    ProductDto updateProduct(Long id, ProductModel productModel);

    void deleteProduct(Long id);




}
