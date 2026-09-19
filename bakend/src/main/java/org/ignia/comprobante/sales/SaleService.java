package org.ignia.comprobante.sales;

import org.ignia.comprobante.categories.CategoryRepository;
import org.ignia.comprobante.productos.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaleService implements ISalesService{

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private ProductRepository productRepositoty;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<SaleDTO> getAllSales() {
        return saleRepository.findAll()
                .stream()
                .map(SaleMapper::toSaleDTO)
                .toList();
    }

    @Override
    public Page<SaleDTO> page(Pageable pageable) {
        return null;
    }

    @Override
    public Page<SaleDTO> page(String search, Pageable pageable) {
        return null;
    }

    @Override
    public SaleDTO getById(Long id) {
        return saleRepository.findById(id).orElse(null);
    }

    @Override
    public SaleDTO createSale(SaleDTO saleDTO) {

        return null;
    }

    @Override
    public SaleDTO updateSale(Long id, SaleDTO saleDTO) {
        return null;
    }

    @Override
    public void deleteSale(Long id) {

    }
}
