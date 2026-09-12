package org.ignia.comprobante.cliente;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface ClientRepository extends JpaRepository<ClientModel, Long> {

    @Query("SELECT c FROM ClientModel c WHERE c.dni = ?1")
    Optional<ClientModel> findByDni(String dni);

    @Query("SELECT c FROM ClientModel c WHERE c.name LIKE %?1%")
    Page<ClientModel> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT c FROM ClientModel c WHERE c.id = ?1")
    Optional<ClientModel> findByClient_Id(Long id, Pageable pageable);

}
