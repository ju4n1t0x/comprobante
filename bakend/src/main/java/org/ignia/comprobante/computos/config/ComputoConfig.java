package org.ignia.comprobante.computos.config;

import java.util.List;

public record ComputoConfig(String key, String name, String description, List<ParameterConfig> parameters, List<ItemConfig> items) {

    public ComputoConfig {
        parameters = parameters == null
                ? List.of()
                : List.copyOf(parameters);

        items = items == null
                ? List.of()
                : List.copyOf(items);
    }
}
