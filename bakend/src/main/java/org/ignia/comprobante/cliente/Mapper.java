package org.ignia.comprobante.cliente;

public class Mapper {

    public static ClientDTO toClientDto(ClientModel clientModel) {
        if (clientModel == null) return null;

        return ClientDTO.builder()
                .dni(clientModel.getDni())
                .name(clientModel.getName())
                .secondName(clientModel.getSecondName())
                .lastName(clientModel.getLastName())
                .telephoneNumber(clientModel.getTelephoneNumber())
                .secondTelephoneNumber(clientModel.getSecondTelephoneNumber())
                .emailAddress(clientModel.getEmailAddress())
                .province(clientModel.getProvince())
                .postalCode(clientModel.getPostalCode())
                .city(clientModel.getCity())
                .address(clientModel.getAddress())
                .cuit(clientModel.getCuit())
                .build();



    }
}
