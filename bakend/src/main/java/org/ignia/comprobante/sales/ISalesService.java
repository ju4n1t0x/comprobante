package org.ignia.comprobante.sales;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ISalesService {

    List<SaleDTO> getAllSales();

    Page<SaleDTO> page(Pageable pageable);

    Page<SaleDTO> page(String search,  Pageable pageable);

    SaleDTO getById(Long id);

    SaleDTO createSale(SaleDTO saleDTO);

    SaleDTO updateSale(Long id, SaleDTO saleDTO);

    void deleteSale(Long id);

}
