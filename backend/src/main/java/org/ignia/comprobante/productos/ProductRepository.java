package org.ignia.comprobante.productos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductModel, Long> {

    @Query("select p.categoria.id as categoryId, count(p) as cnt from ProductModel p group by p.categoria.id")
    List<CategoryProductCount> countsByCategory();

    long countByStock(int stock);

    long countByCategoria_Id(Long categoriaId);
}
