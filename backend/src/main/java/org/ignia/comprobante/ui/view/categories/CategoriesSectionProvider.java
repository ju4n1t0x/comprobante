package org.ignia.comprobante.ui.view.categories;

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
public class CategoriesSectionProvider implements SectionProvider {

    private final ICategoryService categoryService;
    private final IProductService productService;

    public CategoriesSectionProvider(ICategoryService categoryService, IProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @Override
    public SectionId sectionId() {
        return SectionId.CATEGORIAS;
    }

    @Override
    public SectionDescriptor descriptor() {
        return new SectionDescriptor(sectionId(), "/fxml/categories/categories.fxml", this::sidebarConfig);
    }

    private SidebarConfig sidebarConfig() {
        long total = categoryService.countCategories();
        Map<Long, Long> counts = productService.countsByCategory();

        List<SidebarItem> items = new ArrayList<>();
        items.add(new SidebarItem("all", "Todas las categorías", total, SidebarItem.Tone.DEFAULT));
        for (CategoryDto category : categoryService.getAllCategories()) {
            items.add(new SidebarItem(String.valueOf(category.getId()), category.getName(),
                    counts.getOrDefault(category.getId(), 0L), SidebarItem.Tone.DEFAULT));
        }

        return new SidebarConfig("CATEGORÍAS", items, new SummaryCard("RUBROS", String.valueOf(total), "en catálogo"));
    }
}
