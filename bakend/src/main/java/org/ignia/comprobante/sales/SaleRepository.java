package org.ignia.comprobante.sales;

import org.ignia.comprobante.sales.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface SaleRepository extends JpaRepository<SaleModel, Long> {

    @Query(
        "SELECT s FROM SaleModel s LEFT JOIN s.client c "+
        "WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :q, '%'))  "+
        "OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :q, '%')) "+
        "OR CAST(s.id AS string) LIKE CONCAT('%', :q, '%') ")
    Page<SaleModel> search(@Param("q") String q, Pageable pageable);

    long countByState(State state);

    @Query("SELECT s.state, COUNT(s) FROM SaleModel s GROUP BY s.state")
    Map<State, Long> countGroupedByState();

}
