package org.ignia.comprobante.ItemComputo;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemComputoDTO {

    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal unitPrice;
    private BigDecimal quantity;
    private UnitType unitType;
}
