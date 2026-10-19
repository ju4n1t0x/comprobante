package org.ignia.comprobante.cotizacion;

import jakarta.persistence.*;
import lombok.*;
import org.ignia.comprobante.cliente.ClientModel;
import org.ignia.comprobante.itemCotizacion.ItemCotizacionModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CotizacionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String computoKey;
    private String computoName;
    private BigDecimal areaM2;
    private BigDecimal perimeterML;
    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal total;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private State state;

    @ManyToOne(fetch = FetchType.LAZY)
    private ClientModel client;

    @Builder.Default
    @OneToMany(mappedBy = "cotizacion", cascade=CascadeType.ALL, orphanRemoval = true)
    List<ItemCotizacionModel> items = new ArrayList<>();
}
