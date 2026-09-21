package org.ignia.comprobante.ItemComputo;

import jakarta.persistence.*;
import lombok.*;
import org.ignia.comprobante.computos.ComputoModel;
import org.ignia.comprobante.productos.ProductModel;

import java.math.BigDecimal;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemComputoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ComputoModel computo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ProductModel product;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnitType unitType;
}
