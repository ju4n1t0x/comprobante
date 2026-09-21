package org.ignia.comprobante.computos;

import lombok.*;
import org.ignia.comprobante.ItemComputo.ItemComputoDTO;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComputoDTO {

    private Long id;
    private String name;
    private String description;
    private int itemCount;

    @Builder.Default
    private List<ItemComputoDTO> items = new ArrayList<>();
}
