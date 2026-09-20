package org.ignia.comprobante.user;

public class UserMapper {

    public static UserDTO toUserDto(UserModel userModel){
        if (userModel == null) return null;

        return UserDTO.builder()
                .id(userModel.getId())
                .userName(userModel.getUserName())
                .password(userModel.getPassword())
                .email(userModel.getEmail())
                .role(userModel.getRole())
                .telephoneNumber(userModel.getTelephoneNumber())
                .secondTelephoneNumber(userModel.getSecondTelephoneNumber())
                .province(userModel.getProvince())
                .city(userModel.getCity())
                .address(userModel.getAddress())
                .postalCode(userModel.getPostalCode())
                .build();


    }
}
