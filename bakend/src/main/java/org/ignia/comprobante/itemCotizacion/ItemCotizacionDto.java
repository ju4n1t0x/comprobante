package org.ignia.comprobante.itemCotizacion;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCotizacionDto {

    private Long id;
    private String productKey;
    private String productUID;
    private String productName;
    private String unitType;
    private BigDecimal quantity;
    private int quantityDecimals;
    private BigDecimal unitPrice;
    private BigDecimal subTotal;
}
