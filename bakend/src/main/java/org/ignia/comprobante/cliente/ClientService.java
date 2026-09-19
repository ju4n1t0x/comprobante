package org.ignia.comprobante.cliente;

import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import org.ignia.comprobante.exception.ConflictException;
import org.ignia.comprobante.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClientService implements IClientService{

    private final ClientRepository clientRepository;
    private final Validator validator;

    public ClientService(ClientRepository clientRepository, Validator validator) {
        this.clientRepository = clientRepository;
        this.validator = validator;
    }

    private void validate(ClientDTO dto) {
        var violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String msg = violations.stream()
                    .map(v -> v.getPropertyPath() + " " + v.getMessage())
                    .sorted().collect(Collectors.joining(", "));
            throw new IllegalStateException(msg);
        }
        boolean duplicate = clientRepository.findAllByDni(dto.getDni().trim()).stream()
                .anyMatch(c -> dto.getId() == null || !c.getId().equals(dto.getId()));
        if (duplicate) {
            throw new ConflictException("Ya existe un cliente con el dni " + dto.getDni());
        }
    }

    //traemos el listado de clientes
    @Override
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream().map(Mapper::toClientDto).toList();
    }

    @Override
    public Page<ClientDTO> page(String search, Pageable pageable) {
        return page(search, null, pageable);
    }
    //paginamos el listado de clientes
    @Override
    public Page<ClientDTO> page(String search, String city, Pageable pageable) {
        boolean hasSearch = search != null && !search.isBlank();
        boolean hasCity = city != null && !city.isBlank();
        if (!hasSearch && !hasCity) return clientRepository.findAll(pageable).map(Mapper::toClientDto);
        if (!hasSearch) return clientRepository.findByCityIgnoreCase(city.trim(), pageable).map(Mapper::toClientDto);
        String q = search.trim();
        if (!hasCity) return clientRepository.search(q, pageable).map(Mapper::toClientDto);
        return clientRepository.searchInCity(q, city.trim(), pageable).map(Mapper::toClientDto);
    }

    //traemos un cliente por id
    @Override
    public ClientDTO getById(Long id) {
        var client = clientRepository.findById(id);
        if (!client.isPresent()) {
            throw new RuntimeException("No existe el cliente con dni" + id);
        }
        return Mapper.toClientDto(client.get());
    }

    //guardamos un cliente
    @Override
    public ClientDTO saveClient(ClientDTO clientDTO) {
        validate(clientDTO);
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
        validate(clientDTO);
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
    @Transactional
    @Override
    public void deleteClient(Long id) {
        ClientModel clientModel = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe el cliente con id " + id));
        if (clientModel.getListSales() != null && !clientModel.getListSales().isEmpty()){
            throw new ConflictException("No se puede eliminar: tiene ventas asociadas");
        }
        clientRepository.delete(clientModel);
    }

    //buscamos un cliente por dni
    @Override
    public ClientDTO findByDni(String dni) {
        return clientRepository.findAllByDni(dni).stream()
                .findFirst()
                .map(Mapper::toClientDto)
                .orElseThrow(() -> new NotFoundException("No existe el cliente con dni" + dni));

    }

    @Override
    public Map<String, Long> countsByCity() {
        return clientRepository.countsByCity().stream()
                .collect(Collectors.toMap(
                        c -> c.getCity() == null || c.getCity().isBlank() ? "Sin ciudad" : c.getCity().trim(),
                        CityClientCount::getCnt,
                        Long::sum,
                        LinkedHashMap::new));
    }

    @Override
    public long countClients() {
        return clientRepository.count();
    }


}
