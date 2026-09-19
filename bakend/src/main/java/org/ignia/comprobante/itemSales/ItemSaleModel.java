package org.ignia.comprobante.itemSales;

import jakarta.persistence.*;
import lombok.*;
import org.ignia.comprobante.productos.ProductModel;
import org.ignia.comprobante.sales.SaleModel;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemSaleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nameItem;
    private Integer quantity;
    private BigDecimal unitPrice;

    //relacion con producto
    @ManyToOne(fetch = FetchType.LAZY)
    private ProductModel product;

    //relacion con venta
    @ManyToOne(fetch = FetchType.LAZY)
    private SaleModel sale;

}
