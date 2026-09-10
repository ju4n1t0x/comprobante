package org.ignia.comprobante.categories;

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
    private long productCount;
}
