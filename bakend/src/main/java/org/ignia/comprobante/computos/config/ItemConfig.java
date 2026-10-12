package org.ignia.comprobante.computos.config;

import org.ignia.comprobante.computos.UnitType;

public record ItemConfig(String key, String productUid, UnitType unitType, Integer quantityDecimals, String note, QuantityRuleConfig rule) {

    public ItemConfig {
        quantityDecimals = quantityDecimals ==  null
                ? 0
                : quantityDecimals;
    }
}
