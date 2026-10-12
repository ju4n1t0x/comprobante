package org.ignia.comprobante.computos.result;

import java.math.BigDecimal;
import java.util.List;

public record ComputedQuote(String computoKey, String computoName, List<ComputedItem> items, BigDecimal subtotal, BigDecimal iva, BigDecimal total) {
}
