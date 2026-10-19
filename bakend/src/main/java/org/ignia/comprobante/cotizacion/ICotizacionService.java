package org.ignia.comprobante.cotizacion;

import org.ignia.comprobante.computos.result.ComputedQuote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ICotizacionService {

    CotizacionDto createCotizacion(ComputedQuote quote, Map<String, BigDecimal> parameters, Long clientId);

    List<CotizacionDto> getAllCotizaciones();

    List<CotizacionDto> findByClientId(Long clientId);

    List<CotizacionDto> findByState(State state);

    CotizacionDto updateState(Long id, State state);

    Page<CotizacionDto> page(State state, String search, Pageable pageable);

    long countByState(State state);


}
