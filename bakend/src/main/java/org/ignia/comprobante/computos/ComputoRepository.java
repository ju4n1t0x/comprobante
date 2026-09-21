package org.ignia.comprobante.computos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComputoRepository extends JpaRepository<ComputoModel, Long> {

    Page<ComputoModel> findByNameContainingIgnoreCase(String name, Pageable pageable);

    boolean existsByNameIgnoreCase(String name);
}
