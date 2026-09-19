package org.ignia.comprobante.cliente;

import jakarta.persistence.*;
import lombok.*;
import org.ignia.comprobante.sales.SaleModel;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String dni;
    private String name;
    private String secondName;
    private String lastName;
    private String telephoneNumber;
    private String secondTelephoneNumber;
    private String emailAddress;
    private String province;
    private String postalCode;
    private String city;
    private String address;
    private String cuit;


    //relacion con venta
    @OneToMany(mappedBy = "client")
    private List<SaleModel> listSales;


}