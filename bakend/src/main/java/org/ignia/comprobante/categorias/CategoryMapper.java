package org.ignia.comprobante.categorias;

public class CategoryMapper {

    public static CategoryDto toCategoryDto(CategoryModel categoryModel){
        if (categoryModel == null) return null;

        return CategoryDto.builder()
                .id(categoryModel.getId())
                .categoryUID(categoryModel.getCategoryUID())
                .name(categoryModel.getName())
                .description(categoryModel.getDescription())
                .build();
    }
}
