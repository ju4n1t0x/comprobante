package org.ignia.comprobante.sales;

import lombok.*;
import org.ignia.comprobante.cliente.ClientDTO;
import org.ignia.comprobante.itemSales.ItemSaleDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleDTO {

    private Long id;
    //condicion de iva
    private String typeIva;
    private BigDecimal baseImponible; // calcular sobre precio unitario sin descuento ni iva.
    private BigDecimal discount; //descuento se hace sobre el valor con iva
    private BigDecimal iva; // calculado sobre el total de la base imponible
    private BigDecimal totalPrice;
    private LocalDate date;
    private State state;

    //relacion con cliente
    private Long clientId;
    private String nameClient;
    private String dniClient;
    private String cityClient;
    private String stateClient;

    //relacion con usuario
    private Long userId;
    private String userName;

    //relacion con item venta
    @Builder.Default
    private List<ItemSaleDTO> itemsSales = new ArrayList<>();

}
