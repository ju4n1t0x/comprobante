package org.ignia.comprobante.sales;


import org.ignia.comprobante.itemSales.ItemSaleDTO;
import org.ignia.comprobante.itemSales.ItemSaleMapper;

import java.math.BigDecimal;

import java.util.List;
import java.util.Objects;

public final class SaleMapper {

    private SaleMapper(){
    }

    private static final BigDecimal IVA_RATE = new BigDecimal("0.21");

    public static SaleDTO toSaleDTO(SaleModel saleModel) {

        if (saleModel == null) return null;

        List<ItemSaleDTO> items = saleModel.getItemsSales()
                .stream()
                .map(ItemSaleMapper::toItemSaleDTO)
                .toList();

        BigDecimal baseImponible = items.stream()
                .map(ItemSaleDTO::getSubTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = saleModel.getDiscount();

        BigDecimal iva = calcIva(baseImponible);

        BigDecimal total = calcTotal(baseImponible, iva, discount);

        return SaleDTO.builder()
                .id(saleModel.getId())
                .typeIva(saleModel.getTypeIva())
                .baseImponible(baseImponible)
                .discount(discount)
                .iva(iva)
                .totalPrice(total)
                .date(saleModel.getDate())
                .state(saleModel.getState())
                .itemsSales(items)
                .build();
    }

    private static BigDecimal calcIva(BigDecimal baseImponible) {
        if (baseImponible == null) return BigDecimal.ZERO;
        return baseImponible.multiply(IVA_RATE);
    }

    private static BigDecimal calcTotal(BigDecimal baseImponible, BigDecimal iva, BigDecimal discount) {
        if (baseImponible == null) return BigDecimal.ZERO;
        BigDecimal total = baseImponible;

        if (iva !=null){
            total = total.add(iva);
        }

        if (discount != null) {
            total = total.subtract(discount);
        }
        return total;
    }

}
