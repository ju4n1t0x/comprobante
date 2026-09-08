package org.ignia.comprobante.ItemVenta;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ignia.comprobante.productos.ProductModel;
import org.ignia.comprobante.ventas.SaleModel;
import org.ignia.comprobante.ventas.State;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemSaleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer quantity;
    private BigDecimal unitPrice;
    @Transient
    private BigDecimal totalPrice;


    //relacion con producto
    @ManyToOne
    private ProductModel product;

    //relacion con venta
    @ManyToOne
    private SaleModel sale;

}
