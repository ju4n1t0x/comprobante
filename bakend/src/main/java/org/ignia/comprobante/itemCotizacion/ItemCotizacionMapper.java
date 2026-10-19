package org.ignia.comprobante.itemCotizacion;

import org.ignia.comprobante.computos.result.ComputedItem;

public final class ItemCotizacionMapper {

    private ItemCotizacionMapper (){
    }

    public static ItemCotizacionDto toItemCotizacionDto(ItemCotizacionModel model){
        if(model == null) return null;
        return ItemCotizacionDto.builder()
                .id(model.getId())
                .productKey(model.getProductKey())
                .productUID(model.getProductUID())
                .productName(model.getProductName())
                .unitType(model.getUnitType())
                .quantity(model.getQuantity())
                .quantityDecimals(model.getQuantityDecimals())
                .unitPrice(model.getUnitPrice())
                .subTotal(model.getSubTotal())
                .build();
    }

    public static ItemCotizacionModel toItemCotizacionModel(ComputedItem item){
        if(item == null) return null;
        return ItemCotizacionModel.builder()
                .productKey(item.key())
                .productUID(item.productUid())
                .productName(item.productName())
                .unitType(item.unitType())
                .quantity(item.quantity())
                .quantityDecimals(item.quantityDecimals())
                .unitPrice(item.unitPrice())
                .subTotal(item.subTotal())
                .build();
    }
}
