package org.ignia.comprobante.sales;


import org.ignia.comprobante.itemSales.ItemSaleDTO;
import org.ignia.comprobante.itemSales.ItemSaleMapper;

import java.math.BigDecimal;

import java.util.List;
import java.util.Objects;

public final class SaleMapper {

    private SaleMapper(){
    }


    public static SaleDTO toSaleDTO(SaleModel saleModel) {

        if (saleModel == null) return null;

        List<ItemSaleDTO> items = saleModel.getItemsSales()
                .stream()
                .map(ItemSaleMapper::toItemSaleDTO)
                .toList();


        return SaleDTO.builder()
                .id(saleModel.getId())
                .typeIva(saleModel.getTypeIva())
                .baseImponible(saleModel.getBaseImponible())
                .discount(saleModel.getDiscount())
                .iva(saleModel.getIva())
                .totalPrice(saleModel.getTotalPrice())
                .date(saleModel.getDate())
                .state(saleModel.getState())
                .itemsSales(items)
                .clientId(saleModel.getClient().getId())
                .nameClient(saleModel.getClient().getName())
                .dniClient(saleModel.getClient().getDni())
                .cityClient(saleModel.getClient().getCity())
                .stateClient(saleModel.getClient().getAddress())
                .userId(saleModel.getUser().getId())
                .userName(saleModel.getUser().getUserName())
                .build();
    }


}
