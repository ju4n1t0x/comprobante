package org.ignia.comprobante.cliente;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface ClientRepository extends JpaRepository<ClientModel, Long> {

    //metodo para filtrar clientes por dni
    @Query("SELECT c FROM ClientModel c WHERE c.dni = ?1")
    Optional<ClientModel> findByDni(String dni);

    @Query("""
            SELECT c FROM ClientModel c WHERE LOWER(c.name)
             LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(c.secondName) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :q, '%'))
            """)
    Page<ClientModel> search(@Param("q") String q, Pageable pageable);

    Page<ClientModel> findByCityIgnoreCase(String city, Pageable pageable);

    @Query("""
            SELECT c FROM ClientModel c
            WHERE LOWER(c.city) = LOWER(:city)
              AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(c.secondName) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :q, '%')))
            """)

    Page<ClientModel> searchInCity(@Param("q") String q, @Param("city") String city, Pageable pageable);

    @Query("SELECT c.city AS city, COUNT(c) AS cnt FROM ClientModel c GROUP BY c.city")
    List<CityClientCount> countsByCity();


}
