package org.ignia.comprobante.itemSales;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemSaleDTO {

    private Long id;
    private Long productId;
    private String nameItem;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subTotal;

}
