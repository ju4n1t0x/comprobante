package org.ignia.comprobante.computos;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.ignia.comprobante.computos.config.ComputoConfig;
import org.ignia.comprobante.computos.config.ItemConfig;
import org.ignia.comprobante.computos.config.ParameterConfig;
import org.ignia.comprobante.computos.config.QuantityRuleConfig;
import org.ignia.comprobante.computos.result.ComputedItem;
import org.ignia.comprobante.computos.result.ComputedQuote;
import org.ignia.comprobante.productos.ProductModel;
import org.ignia.comprobante.productos.ProductRepository;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class ComputoCatalogService implements IComputoCatalogService{

    private static final String CONFIG_LOCATION = "classpath*:configuracionesComputos/*.json";

    private static final BigDecimal IVA = new BigDecimal("0.21");
    private static final int CALCULATION_SCALE = 6;

    private final ProductRepository productRepository;
    private final List<ComputoConfig> computos;

    public ComputoCatalogService(ObjectMapper objectMapper, ProductRepository productRepository){
        this.productRepository = productRepository;
        this.computos = load(objectMapper);
    }

    public static BigDecimal getIVA() {
        return IVA;
    }

    @Override
    public List<ComputoConfig> findAll() {
        return computos;
    }

    @Override
    public long count() {
        return computos.size();
    }

    @Override
    public ComputoConfig findByKey(String key) {
        return computos.stream()
                .filter(computo -> computo.key().equals(key))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Computo no encontrado: " + key));
    }

    @Override
    public Page<ComputoConfig> page(String search, Pageable pageable) {
        String query = search == null ? "" : search.trim().toLowerCase();

        List<ComputoConfig> filtered = computos.stream()
                .filter(computo -> query.isBlank() || computo.name().toLowerCase().contains(query))
                .toList();

        int from = (int) Math.min(pageable.getOffset(), filtered.size());

        int to = Math.min(from + pageable.getPageSize(), filtered.size());

        List<ComputoConfig> content = filtered.subList(from, to);

        return new PageImpl<>(
                content,
                pageable,
                filtered.size()
        );
    }

    @Override
    public ComputedQuote calculate(ComputoConfig computo, Map<String, BigDecimal> parameters) {
        Map<String, BigDecimal> values = new HashMap<>(parameters);
        List<ComputedItem> resultItems = new ArrayList<>();

        BigDecimal subtotal = BigDecimal.ZERO;

        for (ItemConfig itemConfig : computo.items()) {
            QuantityRuleConfig rule = itemConfig.rule();

            BigDecimal sourceValue = values.get(rule.source());

            if (sourceValue == null) {
                throw new IllegalArgumentException("No existe el valor para : " + rule.source());
            }


            BigDecimal quantity = calculateQuantity(rule, sourceValue);

            ProductModel product = findProduct(itemConfig.productUid());

            BigDecimal unitPrice = product.getUnitPrice() == null
                    ? BigDecimal.ZERO
                    : product.getUnitPrice();

            BigDecimal itemSubtotal = quantity.multiply(unitPrice);

            resultItems.add(
                    new ComputedItem(
                            itemConfig.key(),
                            product.getProductUID(),
                            product.getNameProduct(),
                            itemConfig.unitType(),
                            quantity,
                            itemConfig.quantityDecimals(),
                            unitPrice,
                            itemSubtotal
                    )
            );

            values.put(itemConfig.key(), quantity);
            subtotal = subtotal.add(itemSubtotal);
        }

        BigDecimal iva = subtotal.multiply(IVA);
        BigDecimal total = subtotal.add(iva);

        return new ComputedQuote(
                computo.key(),
                computo.name(),
                List.copyOf(resultItems),
                subtotal,
                iva,
                total
        );
    }

    private BigDecimal calculateQuantity(QuantityRuleConfig rule, BigDecimal sourceValue) {

        BigDecimal multiplier = requireValue(rule.multiplier(), "multiplier");

        BigDecimal divisor = requireValue(rule.divisor(), "divisor");

        BigDecimal factor = requireValue(rule.factor(), "factor");

        if (divisor.signum() == 0){
            throw new IllegalArgumentException("El divisor no puede ser cero");
        }

        return sourceValue
                .multiply(multiplier)
                .divide(divisor, CALCULATION_SCALE, RoundingMode.HALF_UP)
                .multiply(factor)
                .setScale(CALCULATION_SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal requireValue(BigDecimal value, String field) {

        if (value == null){
            throw new IllegalArgumentException("La regla no tiene valor para: " + field);
        }
        return value;
    }

    private ProductModel findProduct(String productUid) {
        return productRepository.findByProductUID(productUid)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + productUid));
    }

    private List<ComputoConfig> load(ObjectMapper objectMapper) {

        try{
            Resource[] resources =
                    new PathMatchingResourcePatternResolver()
                            .getResources(CONFIG_LOCATION);

            List<ComputoConfig> loaded = new ArrayList<>();

            for (Resource resource : resources) {
                try (InputStream inputStream = resource.getInputStream()){
                    ComputoConfig config = objectMapper.readValue(inputStream, ComputoConfig.class);

                    validate(config);
                    loaded.add(config);
                }
            }

            loaded.sort(
                    Comparator.comparing(ComputoConfig::name, String.CASE_INSENSITIVE_ORDER)
            );

            return List.copyOf(loaded);
        } catch (IOException ex) {
            throw new IllegalStateException(
                    "No se pudieron cargar las configuraciones de computos",
                    ex
            );
        }
    }

    private void validate(ComputoConfig config) {

        if (config.key() == null || config.key().isBlank()) {
            throw new IllegalArgumentException("Un computo no tiene key");
        }

        if (config.name() == null || config.name().isBlank()) {
            throw new IllegalArgumentException("Un computo no tiene name: " + config.key());
        }

        Set<String> parameterKeys = new HashSet<>();

        for (ParameterConfig parameter : config.parameters()) {
            if (!parameterKeys.add(parameter.key())) {
                throw new IllegalStateException("Parametro duplicado en " + config.key() + ": " + parameter.key());
            }
        }
        Set<String> itemKeys = new HashSet<>();
        Set<String> availableSources = new HashSet<>(parameterKeys);

        for (ItemConfig item : config.items()) {
            if (!itemKeys.add(item.key())) {
                throw new IllegalStateException("Item duplicado en " + config.key() + ": " + item.key());
            }

            findProduct(item.productUid());

            if (!availableSources.contains(item.rule().source())) {
                throw new IllegalStateException("El item " + item.key() + "referencia una fuente inexistente o futura: " + item.rule().source());
            }
            availableSources.add(item.key());
        }
    }
}
