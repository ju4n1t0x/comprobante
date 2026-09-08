package org.ignia.comprobante.productos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private Integer productUID;
    private String nameProduct;
    private Double unitPrice;
    private Double profitPercentage;
    private Integer stock;
    private boolean active;

}
