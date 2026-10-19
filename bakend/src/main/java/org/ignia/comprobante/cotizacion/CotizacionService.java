package org.ignia.comprobante.cotizacion;

import org.ignia.comprobante.cliente.ClientModel;
import org.ignia.comprobante.cliente.ClientRepository;
import org.ignia.comprobante.computos.result.ComputedQuote;
import org.ignia.comprobante.exception.NotFoundException;
import org.ignia.comprobante.itemCotizacion.ItemCotizacionMapper;
import org.ignia.comprobante.itemCotizacion.ItemCotizacionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CotizacionService implements ICotizacionService{

    @Autowired
    private CotizacionRepository cotizacionRepository;

    @Autowired
    private ClientRepository clientRepository;


    @Override
    @Transactional
    public CotizacionDto createCotizacion(ComputedQuote quote, Map<String, BigDecimal> parameters, Long clientId) {

        List<ItemCotizacionModel> items = new ArrayList<>();
        for(var computedItem : quote.items()) {
            items.add(ItemCotizacionMapper.toItemCotizacionModel(computedItem));
        }

        ClientModel client = null;
        if (clientId != null) {
            client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));
        }

        CotizacionModel cotizacion = CotizacionModel.builder()
                .computoKey(quote.computoKey())
                .computoName(quote.computoName())
                .areaM2(parameters.get("m2"))
                .perimeterML(parameters.get("ml"))
                .subtotal(quote.subtotal())
                .iva(quote.iva())
                .total(quote.total())
                .createdAt(LocalDateTime.now())
                .state(State.PENDIENTE)
                .client(client)
                .build();

        for (ItemCotizacionModel item: items) {
            item.setCotizacion(cotizacion);
            cotizacion.getItems().add(item);
        }

        return CotizacionMapper.toCotizacionDto(cotizacionRepository.save(cotizacion));

    }

    @Override
    public List<CotizacionDto> getAllCotizaciones() {
        List<CotizacionDto> result = new ArrayList<>();
        for (CotizacionModel model : cotizacionRepository.findAllOrdered()) {
            result.add(CotizacionMapper.toCotizacionDto(model));
        }
        return result;
    }

    @Override
    public List<CotizacionDto> findByClientId(Long clientId) {
        List<CotizacionDto> result = new ArrayList<>();
        for (CotizacionModel model : cotizacionRepository.findByClientId(clientId)) {
            result.add(CotizacionMapper.toCotizacionDto(model));
        }
        return result;
    }

    @Override
    public List<CotizacionDto> findByState(State state) {
        List<CotizacionDto> result = new ArrayList<>();
        for (CotizacionModel model : cotizacionRepository.findByState(state)) {
            result.add(CotizacionMapper.toCotizacionDto(model));
    }
        return result;
    }

    @Override
    @Transactional
    public CotizacionDto updateState(Long id, State state) {
        CotizacionModel model = cotizacionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cotización no encontrada"));
        model.setState(state);
        return CotizacionMapper.toCotizacionDto(cotizacionRepository.save(model));
    }


}
