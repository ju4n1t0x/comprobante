package org.ignia.comprobante.cliente;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IClientService {

    List<ClientDTO> getAllClients();

    Page<ClientDTO> page(String search, Pageable pageable);

    Page<ClientDTO> page(String search, Long clientId, Pageable pageable);

    ClientDTO getById(Long id);

    ClientDTO saveClient(ClientDTO clientDTO);

    ClientDTO updateClient(Long id, ClientDTO clientDTO);

    void deleteClient(Long id);

    ClientDTO findByDni(String dni);

}
