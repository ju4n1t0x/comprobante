package org.ignia.comprobante.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ignia.comprobante.sales.SaleModel;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private String password;
    private String email;
    private Role role;
    private String telephoneNumber;
    private String secondTelephoneNumber;
    private String province;
    private String city;
    private String address;
    private String PostalCode;

    //relacion con venta
    @OneToMany(mappedBy = "user")
    private List<SaleModel> listSale;
}
