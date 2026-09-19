package org.ignia.comprobante.productos;

import jakarta.persistence.*;
import lombok.*;
import org.ignia.comprobante.itemSales.ItemSaleModel;
import org.ignia.comprobante.categories.CategoryModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, unique=true)
    private String productUID;
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
