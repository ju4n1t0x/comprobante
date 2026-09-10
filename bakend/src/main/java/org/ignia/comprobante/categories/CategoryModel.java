package org.ignia.comprobante.categories;

import jakarta.persistence.*;
import lombok.*;
import org.ignia.comprobante.productos.ProductModel;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String categoryUID;
    private String description;



    //relacion con producto
    @OneToMany(mappedBy = "categoria")
    private List<ProductModel> listProduct;



}
