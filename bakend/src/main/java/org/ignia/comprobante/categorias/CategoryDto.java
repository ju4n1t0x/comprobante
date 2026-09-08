package org.ignia.comprobante.categorias;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    private Long id;
    private String categoryUID;
    private String name;
    private String description;
}
