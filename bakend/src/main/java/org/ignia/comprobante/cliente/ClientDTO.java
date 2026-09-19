package org.ignia.comprobante.cliente;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.ignia.comprobante.sales.SaleDTO;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDTO {

    private Long id;

    @NotBlank(message = "El Dni es obligatorio")
    @Pattern(regexp = "\\d{7,8}", message = "El DNI debe tener 7 u 8 digitos")
    private String dni;

    @NotBlank(message = "El nombre es obligatorio")
    @Pattern(regexp = "[A-Za-záéíóúÁÉÍÓÚñÑ]{2,50}", message = "El nombre solo puede contener letras")
    private String name;

    @Pattern(regexp = "|[A-Za-záéíóúÁÉÍÓÚñÑ]{2,50}", message = "El segundo nombre solo puede contener letras y espacios")
    private String secondName;

    @NotBlank(message = "El apellido es obligatorio")
    @Pattern(regexp = "[A-Za-záéíóúÁÉÍÓÚñÑ]{2,50}", message = "El apellido solo puede contener letras")
    private String lastName;

    @NotBlank(message = "El telefono es obligatorio")
    @Pattern(regexp = "\\d{10}", message = "El telefono debe tener 10 digitos")
    private String telephoneNumber;

    @Pattern(regexp = "|\\d{6,15}", message = "Telefono alternativo invalido")
    private String secondTelephoneNumber;

    @NotBlank(message = "El email es obligatorio")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "El email debe ser valido")
    private String emailAddress;

    @NotBlank(message = "La provincia es obligatoria")
    private String province;

    @NotBlank(message = "El codigo postal es obligatorio")
    @Pattern(regexp = "\\d{4}", message = "El codigo postal debe tener 4 digitos")
    private String postalCode;

    @NotBlank(message = "La ciudad es obligatoria")
    private String city;

    @NotBlank(message = "Dirección es obligatoria")
    @Pattern(regexp = "[A-Za-zÁÉÍÓÚáéíóúÑñ0-9 .,'\\-°º]{3,50}",
            message = "Dirección inválida (letras, números y espacios, 3-50)")

    private String address;

    @NotBlank(message = "El cuit es obligatorio")
    @Pattern(regexp = "\\d{2}-\\d{8}-\\d",
            message = "CUIT debe tener formato 20-12345678-9 (solo números y guiones)")

    private String cuit;

    private List<SaleDTO> listSales;

}
