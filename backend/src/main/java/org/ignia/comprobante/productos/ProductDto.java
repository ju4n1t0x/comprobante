package org.ignia.comprobante.productos;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {

    private Integer productUID;
    private String nameProduct;
    private BigDecimal unitPrice;
    private BigDecimal profitPercentage;
    private Integer stock;
    private boolean active;
    private String categoryName;

}
