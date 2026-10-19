package org.ignia.comprobante.itemCotizacion;

import jakarta.persistence.*;
import lombok.*;
import org.ignia.comprobante.computos.UnitType;
import org.ignia.comprobante.cotizacion.CotizacionModel;

import java.math.BigDecimal;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCotizacionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productKey;
    private String productUID;
    private String productName;

    @Enumerated(EnumType.STRING)
    private UnitType unitType;

    private BigDecimal quantity;
    private int quantityDecimals;
    private BigDecimal unitPrice;
    private BigDecimal subTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    private CotizacionModel cotizacion;
}
