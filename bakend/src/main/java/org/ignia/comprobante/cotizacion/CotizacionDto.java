package org.ignia.comprobante.cotizacion;

import lombok.*;
import org.ignia.comprobante.itemCotizacion.ItemCotizacionDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CotizacionDto {

    private Long id;
    private String computoKey;
    private String computoName;
    private BigDecimal areaM2;
    private BigDecimal perimeterML;
    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal total;
    private LocalDateTime createdAt;
    private State state;
    private Long clientId;
    private String clientName;
    private String clientLastName;
    private String clientDni;
    private List<ItemCotizacionDto> items;
}
