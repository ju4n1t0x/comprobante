package org.ignia.comprobante.computos.config;

import java.math.BigDecimal;

public record QuantityRuleConfig(String source, BigDecimal multiplier, BigDecimal divisor, BigDecimal factor) {
}
