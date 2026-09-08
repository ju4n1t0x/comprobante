package org.ignia.comprobante.productos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ignia.comprobante.ItemVenta.ItemSaleModel;
import org.ignia.comprobante.categorias.CategoryModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer productUID;
    private String nameProduct;
    private BigDecimal unitPrice;
    private BigDecimal profitPercentage;
    private Integer stock;
    private boolean active;

    //relacion con item venta
    @OneToMany(mappedBy = "product")
    private List<ItemSaleModel> itemSale = new ArrayList<>();

    //relacion con categoria
    @ManyToOne
    private CategoryModel categoria;



}
