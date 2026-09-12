package org.ignia.comprobante.cliente;

import org.ignia.comprobante.exception.ConflictException;
import org.ignia.comprobante.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ClientService implements IClientService{

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    //traemos el listado de clientes
    @Override
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream().map(Mapper::toClientDto).toList();
    }

    //paginamos el listado de clientes
    @Override
    public Page<ClientDTO> page(Pageable pageable) {
        return clientRepository.findAll(pageable)
                .map(Mapper::toClientDto);
    }

    //traemos un cliente por id
    @Override
    public ClientDTO getById(Long id) {
        var client = clientRepository.findById(id);
        if (!client.isPresent()) {
            throw new RuntimeException("No existe el cliente con dni" + id);
        }
        return clientRepository.findById(id)
                .stream()
                .map(Mapper::toClientDto)
                .findFirst()
                .orElse(null);
    }

    //guardamos un cliente
    @Override
    public ClientDTO saveClient(ClientDTO clientDTO) {

        ClientModel clientModel = ClientModel.builder()
                .dni(clientDTO.getDni())
                .name(clientDTO.getName())
                .secondName(clientDTO.getSecondName())
                .lastName(clientDTO.getLastName())
                .telephoneNumber(clientDTO.getTelephoneNumber())
                .secondTelephoneNumber(clientDTO.getSecondTelephoneNumber())
                .emailAddress(clientDTO.getEmailAddress())
                .province(clientDTO.getProvince())
                .postalCode(clientDTO.getPostalCode())
                .city(clientDTO.getCity())
                .address(clientDTO.getAddress())
                .cuit(clientDTO.getCuit())
                .build();

        return Mapper.toClientDto(clientRepository.save(clientModel));
    }

    //actualizamos un cliente
    @Override
    public ClientDTO updateClient(Long id, ClientDTO clientDTO) {
        ClientModel clientModel = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe el cliente con id " + id));

        clientModel.setDni(clientDTO.getDni());
        clientModel.setName(clientDTO.getName());
        clientModel.setSecondName(clientDTO.getSecondName());
        clientModel.setLastName(clientDTO.getLastName());
        clientModel.setTelephoneNumber(clientDTO.getTelephoneNumber());
        clientModel.setSecondTelephoneNumber(clientDTO.getSecondTelephoneNumber());
        clientModel.setEmailAddress(clientDTO.getEmailAddress());
        clientModel.setProvince(clientDTO.getProvince());
        clientModel.setPostalCode(clientDTO.getPostalCode());
        clientModel.setCity(clientDTO.getCity());
        clientModel.setAddress(clientDTO.getAddress());
        clientModel.setCuit(clientDTO.getCuit());

        return Mapper.toClientDto(clientRepository.save(clientModel));
    }

    //eliminamos un cliente
    @Override
    public void deleteClient(Long id) {
        ClientModel clientModel = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe el cliente con id " + id));
        if (clientModel.getListSales() != null && !clientModel.getListSales().isEmpty()){
            throw new ConflictException("No se puede eliminar: tiene ventas asociadas");
        }
    }

    //buscamos un cliente por dni
    @Override
    public ClientDTO findByDni(String dni) {
        ClientModel client = clientRepository.findByDni(dni)
                .orElseThrow(() -> new NotFoundException("No existe el cliente con dni " + dni));
        return Mapper.toClientDto(client);
    }

    @Override
    public Page<ClientDTO> page(String search, Long clientId, Pageable pageable) {
        boolean hasSearch = search != null && !search.isBlank();

        if (!hasSearch && clientId == null) return page(pageable);
        if (!hasSearch) return clientRepository.findByClient_Id(clientId, pageable).map(Mapper::toClientDto);
        String q = search.trim();
        if (clientId == null) return clientRepository.findByNameContainingIgnoreCase(q, pageable).map(Mapper::toClientDto);
        return clientRepository.findByClient_Id(clientId, pageable).map(Mapper::toClientDto);

    }
}
