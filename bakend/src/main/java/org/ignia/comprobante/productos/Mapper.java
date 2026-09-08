package org.ignia.comprobante.productos;

public class Mapper {

    public static ProductDto toPorudctDTO(ProductModel productModel){
        if (productModel == null) return null;

        return ProductDto.builder()
                .productUID(productModel.getProductUID())
                .nameProduct(productModel.getNameProduct())
                .unitPrice(productModel.getUnitPrice())
                .profitPercentage(productModel.getProfitPercentage())
                .stock(productModel.getStock())
                .active(productModel.setActive())
                .build();
    }
}
