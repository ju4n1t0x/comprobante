package org.ignia.comprobante.user;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

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
    private String postalCode;

}
