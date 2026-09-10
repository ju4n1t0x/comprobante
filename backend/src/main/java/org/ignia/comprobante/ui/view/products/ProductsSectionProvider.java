package org.ignia.comprobante.ui.view.products;

import org.ignia.comprobante.categories.CategoryDto;
import org.ignia.comprobante.categories.ICategoryService;
import org.ignia.comprobante.productos.IProductService;
import org.ignia.comprobante.ui.model.SectionId;
import org.ignia.comprobante.ui.model.SidebarConfig;
import org.ignia.comprobante.ui.model.SidebarItem;
import org.ignia.comprobante.ui.model.SummaryCard;
import org.ignia.comprobante.ui.shell.SectionDescriptor;
import org.ignia.comprobante.ui.shell.SectionProvider;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ProductsSectionProvider implements SectionProvider {

    private final IProductService productService;
    private final ICategoryService categoryService;

    public ProductsSectionProvider(IProductService productService, ICategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @Override
    public SectionId sectionId() {
        return SectionId.PRODUCTOS;
    }

    @Override
    public SectionDescriptor descriptor() {
        return new SectionDescriptor(sectionId(), "/fxml/products/products.fxml", this::sidebarConfig);
    }

    private SidebarConfig sidebarConfig() {
        long total = productService.countProducts();
        long sinStock = productService.countOutOfStock();
        Map<Long, Long> counts = productService.countsByCategory();

        List<SidebarItem> items = new ArrayList<>();
        items.add(new SidebarItem("all", "Todos los productos", total, SidebarItem.Tone.DEFAULT));
        for (CategoryDto category : categoryService.getAllCategories()) {
            items.add(new SidebarItem("cat-" + category.getId(), category.getName(),
                    counts.getOrDefault(category.getId(), 0L), SidebarItem.Tone.DEFAULT));
        }
        items.add(new SidebarItem("nostock", "Sin stock", sinStock, SidebarItem.Tone.WARN));

        return new SidebarConfig("PRODUCTOS", items, new SummaryCard("INVENTARIO", String.valueOf(total), "SKUs activos"));
    }
}
