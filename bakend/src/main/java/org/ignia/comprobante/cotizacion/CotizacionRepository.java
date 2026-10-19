package org.ignia.comprobante.cotizacion;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CotizacionRepository extends JpaRepository<CotizacionModel, Long> {

    @Query("SELECT c FROM CotizacionModel c ORDER BY c.createdAt DESC, c.id DESC")
    List<CotizacionModel> findAllOrdered();

    @Query("SELECT c FROM CotizacionModel c WHERE c.client.id = :clientId ORDER BY c.createdAt DESC, c.id DESC")
    List<CotizacionModel> findByClientId(@Param("clientId") Long clientId);

    @Query("SELECT c FROM CotizacionModel c WHERE c.state = :state ORDER BY c.createdAt DESC, c.id DESC")
    List<CotizacionModel> findByState(@Param("state") State state);

    @Query(value = """
        SELECT c FROM CotizacionModel c WHERE (:state IS NULL OR c.state = :state)
        AND (:id IS NULL OR c.id = :id)
        ORDER BY c.createdAt DESC, c.id DESC
""",
    countQuery = """
        SELECT COUNT(c) FROM CotizacionModel c
        WHERE (:state IS NULL OR c.state = :state)
        AND (:id IS NULL OR c.id = :id)
                """)
    Page<CotizacionModel> search(
            @Param("sate") State state,
            @Param("id") Long id,
            Pageable pageable
    );

    long countByState(State state);
}
