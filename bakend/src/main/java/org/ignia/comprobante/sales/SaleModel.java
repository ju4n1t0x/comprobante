package org.ignia.comprobante.sales;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ignia.comprobante.itemSales.ItemSaleModel;
import org.ignia.comprobante.cliente.ClientModel;
import org.ignia.comprobante.user.UserModel;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String typeIva;
    private BigDecimal baseImponible; // calcular sobre precio unitario sin descuento ni iva.
    private BigDecimal discount; //descuento se hace sobre el valor con iva
    private BigDecimal iva; // calculado sobre el total de la base imponible
    private BigDecimal totalPrice;
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private State state;


    //relacion con cliente
    @ManyToOne
    private ClientModel client;

    //relacion con usuario
    @ManyToOne(fetch = FetchType.LAZY)
    private UserModel user;

    //relacion con item venta
    @OneToMany(mappedBy = "sale",
    cascade = CascadeType.ALL,
    orphanRemoval = true)
    private List<ItemSaleModel> itemsSales = new ArrayList<>();


}
