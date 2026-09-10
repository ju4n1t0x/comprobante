package org.ignia.comprobante.cliente;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ignia.comprobante.ventas.SaleModel;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer dni;
    private String name;
    private String secondName;
    private String lastName;
    private String telephoneNumber;
    private String secondTelephoneNumber;
    private String emailAddress;
    private String province;
    private String PostalCode;
    private String city;
    private String address;
    private String cuit;


    //relacion con venta
    @OneToMany(mappedBy = "client")
    private List<SaleModel> listSales;

}
