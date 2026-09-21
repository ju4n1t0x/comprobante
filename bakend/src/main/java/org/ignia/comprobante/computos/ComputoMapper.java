package org.ignia.comprobante.computos;

import org.ignia.comprobante.ItemComputo.ItemComputoDTO;

import java.util.List;

public class ComputoMapper {

    private ComputoMapper() {}

    public static ComputoDTO toComputoDto(ComputoModel model){
        if (model == null){
            return null;
        }

        List<ItemComputoDTO> items = model.getItems()
                .stream()
                .map(item -> ItemComputoDTO.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getNameProduct())
                        .unitPrice(item.getProduct().getUnitPrice())
                        .quantity(item.getQuantity())
                        .unitType(item.getUnitType())
                        .build())
                .toList();

        return ComputoDTO.builder()
                .id(model.getId())
                .name(model.getName())
                .description(model.getDescription())
                .items(items)
                .build();
    }
}
