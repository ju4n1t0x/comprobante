package org.ignia.comprobante.computos.result;

import org.ignia.comprobante.computos.UnitType;

import java.math.BigDecimal;

public record ComputedItem(String key, String productUid, String productName, UnitType unitType, BigDecimal quantity, int quantityDecimals, BigDecimal unitPrice, BigDecimal subTotal) {

    public ComputedItem {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Cantidad no puede ser negativa");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Precio no puede ser negativo");
        }
        if (subTotal == null || subTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Precio total no puede ser negativo");
        }
    }
}
