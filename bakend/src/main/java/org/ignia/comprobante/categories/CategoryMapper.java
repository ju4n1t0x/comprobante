package org.ignia.comprobante.categories;

public class CategoryMapper {

    public static CategoryDto toCategoryDto(CategoryModel categoryModel) {
        return toCategoryDto(categoryModel, 0L);
    }

    public static CategoryDto toCategoryDto(CategoryModel categoryModel, long productCount) {
        if (categoryModel == null) return null;

        return CategoryDto.builder()
                .id(categoryModel.getId())
                .categoryUID(categoryModel.getCategoryUID())
                .name(categoryModel.getName())
                .description(categoryModel.getDescription())
                .productCount(productCount)
                .build();
    }
}
