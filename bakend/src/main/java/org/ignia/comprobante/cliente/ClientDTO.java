package org.ignia.comprobante.cliente;


import lombok.*;
import org.ignia.comprobante.ventas.SalesDTO;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDTO {

    private Integer dni;
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

    private List<SalesDTO> listSales;

}
