package org.ignia.comprobante.ventas;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ignia.comprobante.ItemVenta.ItemSaleModel;
import org.ignia.comprobante.cliente.ClientModel;
import org.ignia.comprobante.user.UserModel;


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
    private Double baseImponible; // calcular sobre precio unitario sin descuento ni iva.
    private Double discount; //descuento se hace sobre el valor con iva
    private Double iva; // calculado sobre el total de la base imponible
    private Double totalPrice;
    private LocalDate date;
    private State state;


    //relacion con cliente
    @ManyToOne
    private ClientModel client;

    //relacion con usuario
    @ManyToOne
    private UserModel user;

    //relacion con item venta
    @OneToMany(mappedBy = "sale")
    private List<ItemSaleModel> itemSale = new ArrayList<>();

}
