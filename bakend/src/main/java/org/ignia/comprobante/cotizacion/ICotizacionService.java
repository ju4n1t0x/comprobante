package org.ignia.comprobante.cotizacion;

import org.ignia.comprobante.computos.result.ComputedQuote;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ICotizacionService {

    CotizacionDto createCotizacion(ComputedQuote quote, Map<String, BigDecimal> parameters, Long clientId);

    List<CotizacionDto> getAllCotizaciones();

    List<CotizacionDto> findByClientId(Long clientId);

    List<CotizacionDto> findByState(State state);

    CotizacionDto updateState(Long id, State state);



}
