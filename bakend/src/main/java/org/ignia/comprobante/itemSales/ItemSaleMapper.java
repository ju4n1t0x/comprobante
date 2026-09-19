package org.ignia.comprobante.itemSales;

import java.math.BigDecimal;

public final class ItemSaleMapper {

    private ItemSaleMapper() {
        // Private constructor to prevent instantiation
    }
    public static ItemSaleDTO toItemSaleDTO(ItemSaleModel item) {

        if (item == null) {
            return null;
        }

        return ItemSaleDTO.builder()
                .id(item.getId())
                .productId(
                        item.getProduct() != null
                                ? item.getProduct().getId()
                                : null
                )
                .nameItem(item.getNameItem())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subTotal(
                        calculateSubTotal(
                                item.getUnitPrice(),
                                item.getQuantity()
                        )
                )
                .build();
    }

    private static BigDecimal calculateSubTotal(BigDecimal unitPrice, Integer quantity) {
        if (unitPrice == null || quantity == null){
            return BigDecimal.ZERO;
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
