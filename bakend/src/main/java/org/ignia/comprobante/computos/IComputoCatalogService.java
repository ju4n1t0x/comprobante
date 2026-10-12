package org.ignia.comprobante.computos;

import org.ignia.comprobante.computos.config.ComputoConfig;
import org.ignia.comprobante.computos.result.ComputedQuote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IComputoCatalogService {

    List<ComputoConfig> findAll();

    long count();

    ComputoConfig findByKey(String key);

    Page<ComputoConfig> page(String search, Pageable pageable);

    ComputedQuote calculate(ComputoConfig computo, Map<String, BigDecimal> parameters);

}
