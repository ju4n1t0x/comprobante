package org.ignia.comprobante.productos;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ProductMapper {

    public static ProductDto toProductDTO(ProductModel productModel) {
        if (productModel == null) return null;

        return ProductDto.builder()
                .id(productModel.getId())
                .productUID(productModel.getProductUID())
                .nameProduct(productModel.getNameProduct())
                .unitPrice(productModel.getUnitPrice())
                .profitPercentage(productModel.getProfitPercentage())
                .stock(productModel.getStock())
                .active(productModel.isActive())
                .categoryId(productModel.getCategoria() == null
                        ? null
                        : productModel.getCategoria().getId())
                .categoryName(productModel.getCategoria() == null
                        ? null
                        : productModel.getCategoria().getName())
                .totalPrice(calcTotal(productModel.getUnitPrice(), productModel.getProfitPercentage()))
                .build();
    }

    private static BigDecimal calcTotal(BigDecimal unit, BigDecimal profitPercentage) {
        if(unit == null || profitPercentage == null) return null;
        return unit.multiply(BigDecimal.ONE.add(profitPercentage.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)));
    }
}
