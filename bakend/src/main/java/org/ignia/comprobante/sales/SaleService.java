package org.ignia.comprobante.sales;

import org.springframework.transaction.annotation.Transactional;
import org.ignia.comprobante.cliente.ClientModel;
import org.ignia.comprobante.cliente.ClientRepository;
import org.ignia.comprobante.itemSales.ItemSaleDTO;
import org.ignia.comprobante.itemSales.ItemSaleModel;
import org.ignia.comprobante.productos.ProductModel;
import org.ignia.comprobante.productos.ProductRepository;
import org.ignia.comprobante.user.UserModel;
import org.ignia.comprobante.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SaleService implements ISalesService{

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    private static final BigDecimal IVA_RATE = new BigDecimal("0.21");

    @Override
    public List<SaleDTO> getAllSales() {
        return saleRepository.findAll()
                .stream()
                .map(SaleMapper::toSaleDTO)
                .toList();
    }

    @Override
    public Page<SaleDTO> page(Pageable pageable) {
        return saleRepository.findAll(pageable)
                .map(SaleMapper::toSaleDTO);
    }

    @Override
    public Page<SaleDTO> page(String search, Pageable pageable) {
        return saleRepository.search(search, pageable).map(SaleMapper::toSaleDTO);
    }

    @Override
    public SaleDTO getById(Long id) {
        return saleRepository.findById(id)
                .map(SaleMapper::toSaleDTO)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
    }

    @Override
    @Transactional
    public SaleDTO createSale(SaleDTO saleDTO) {
        if (saleDTO == null) {
            throw new RuntimeException("VentaDTO es null");
        }
        if(saleDTO.getItemsSales() == null || saleDTO.getItemsSales().isEmpty()) {
            throw new RuntimeException("La venta debe tener al menos un item");
        }
        if(saleDTO.getClientId() == null) {
            throw new RuntimeException("La venta debe tener un cliente");
        }
        //buscar el cliente
        ClientModel client = clientRepository.findById(saleDTO.getClientId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        //buscar el usuario
        UserModel user = userRepository.findById(saleDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));


        //creamos la venta
        SaleModel saleModel = new SaleModel();
        saleModel.setTypeIva(saleDTO.getTypeIva());
        saleModel.setClient(client);
        saleModel.setDate(saleDTO.getDate());
        saleModel.setUser(user);

        //lista de ventas
        List<ItemSaleModel> itemsSalesModel = new ArrayList<>();

        for (ItemSaleDTO det : saleDTO.getItemsSales()){
            ProductModel product = productRepository.findById(det.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + det.getNameItem()));

            if (product.getStock() < det.getQuantity()) {
                throw new RuntimeException("Stock insuficiente para: "+ det.getNameItem() + " (disponible: " + product.getStock() + ")");
            }

            //crear el detalleVenta
            ItemSaleModel detailSales = new ItemSaleModel();
            detailSales.setProduct(product);
            detailSales.setUnitPrice(det.getUnitPrice());
            detailSales.setQuantity(det.getQuantity());
            detailSales.setSale(saleModel);

            itemsSalesModel.add(detailSales);
        }

        //calculamos los valores
        BigDecimal baseImponible = calcBaseImponible(itemsSalesModel);
        BigDecimal iva = calcIva(baseImponible, saleDTO.getTypeIva());
        BigDecimal total = calcTotalPrice(baseImponible, iva, saleDTO.getDiscount());


        saleModel.setItemsSales(itemsSalesModel);
        saleModel.setBaseImponible(baseImponible);
        saleModel.setIva(iva);
        saleModel.setDiscount(saleDTO.getDiscount());
        saleModel.setTotalPrice(total);


        saleRepository.save(saleModel);

        return SaleMapper.toSaleDTO(saleModel);
    }

    @Override
    @Transactional
    public SaleDTO updateSale(Long id, SaleDTO saleDTO) {

        LocalDate thisDay = LocalDate.now();
        LocalDate dateLimit = thisDay.minusDays(15);

        //buscar si la venta existe
        SaleModel sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        //validar datos de la ventaDTO

        if (sale.getDate().isBefore(dateLimit)) {
            throw new RuntimeException("No se puede actualizar una venta con más de 15 días de antigüedad");
        }

        if (saleDTO.getState() != null) {
            sale.setState(saleDTO.getState());
        }

        if (saleDTO.getTypeIva() != null){
            sale.setTypeIva(saleDTO.getTypeIva());
        }

        if (saleDTO.getDiscount() != null){
            sale.setDiscount(saleDTO.getDiscount());
        }

        BigDecimal baseImponible = calcBaseImponible(sale.getItemsSales());
        BigDecimal iva = calcIva(baseImponible, sale.getTypeIva());
        BigDecimal discount = calcDiscount(baseImponible, iva, sale.getDiscount());
        BigDecimal total = calcTotalPrice(baseImponible, iva, discount);

        sale.setBaseImponible(baseImponible);
        sale.setIva(iva);
        sale.setTotalPrice(total);

        saleRepository.save(sale);

        return SaleMapper.toSaleDTO(sale);
    }

    @Override
    public void deleteSale(Long id) {
        SaleModel sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        saleRepository.delete(sale);

    }

    //sub total de un item
    private static BigDecimal calcSubTotal(BigDecimal unitPrice, Integer quantity) {
        if (unitPrice == null || quantity == null) return BigDecimal.ZERO;
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    //base imponible = suma de subtotales
    private static BigDecimal calcBaseImponible(List<ItemSaleModel> items){
        return items.stream()
                .map(i -> calcSubTotal(i.getUnitPrice(), i.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    //iva segun typeIva
    private static BigDecimal calcIva(BigDecimal baseImponible, String typeIva){
        if (baseImponible == null || typeIva == null) return BigDecimal.ZERO;
        if ("Excento".equals(typeIva)) return BigDecimal.ZERO;
        return baseImponible.multiply(IVA_RATE);
    }
    //importe del descuento -
    //el porcentaje se aplica sobre base imponible + iva
    private static BigDecimal calcDiscount(BigDecimal baseImponible, BigDecimal iva, BigDecimal discountPercentaje){
        if(discountPercentaje == null) return BigDecimal.ZERO;

        BigDecimal subtotal = baseImponible.add(iva);

        return subtotal
                .multiply(discountPercentaje)
                .divide(new BigDecimal("100"));
    }

    //precio total
    private static BigDecimal calcTotalPrice(BigDecimal baseImponible, BigDecimal iva, BigDecimal discount){
        if (baseImponible == null) return BigDecimal.ZERO;
        BigDecimal total = baseImponible;
        if (iva != null) total = total.add(iva);
        if (discount != null) total = total.subtract(discount);
        return total;
    }

    //contar las ventas por estado
    @Override
    public long countByState(State state){
        return saleRepository.countByState(state);
    }

    @Override
    public Map<State, Long> countGroupedByState(){
        return saleRepository.countGroupedByState();
}

}
