package org.ignia.comprobante.categories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryModel, Long> {

     Page<CategoryModel> findByNameContainingIgnoreCase(String name, Pageable pageable);

}
