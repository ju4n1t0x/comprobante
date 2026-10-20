package org.ignia.comprobante.cotizacion;

import org.ignia.comprobante.itemCotizacion.ItemCotizacionDto;
import org.ignia.comprobante.itemCotizacion.ItemCotizacionMapper;
import org.ignia.comprobante.itemCotizacion.ItemCotizacionModel;

import java.util.ArrayList;
import java.util.List;

public final class CotizacionMapper {

    private CotizacionMapper (){
    }

    public static CotizacionDto toCotizacionDto(CotizacionModel model){
        if (model == null) return null;

        List<ItemCotizacionDto> items = new ArrayList<>();
        if (model.getItems() != null) {
            for (ItemCotizacionModel item : model.getItems()) {
                items.add(ItemCotizacionMapper.toItemCotizacionDto(item));
            }
        }

        return CotizacionDto.builder()
                .id(model.getId())
                .computoKey(model.getComputoKey())
                .computoName(model.getComputoName())
                .areaM2(model.getAreaM2())
                .perimeterML(model.getPerimeterML())
                .subtotal(model.getSubtotal())
                .iva(model.getIva())
                .total(model.getTotal())
                .createdAt(model.getCreatedAt())
                .state(model.getState())
                .clientId(model.getClient() != null ? model.getClient().getId() : null)
                .clientName(model.getClient() != null ? model.getClient().getName() : null)
                .clientLastName(model.getClient() != null ? model.getClient().getLastName() : null)
                .clientDni(model.getClient() != null ? model.getClient().getDni() : null)
                .items(items)
                .build();
    }

}
