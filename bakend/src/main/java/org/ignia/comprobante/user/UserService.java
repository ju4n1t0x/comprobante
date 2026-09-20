package org.ignia.comprobante.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements IUserService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<UserDTO> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public Page<UserDTO> page(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserMapper::toUserDto);
    }

    @Override
    public Page<UserDTO> page(String search, Pageable pageable) {
        boolean hasSearch = search != null && !search.isBlank();
        if (hasSearch) {
            return userRepository.findByUsernameContainingOrEmailContaining(search, search, pageable)
                    .map(UserMapper::toUserDto);
        } else {
            return userRepository.findAll(pageable)
                    .map(UserMapper::toUserDto);
        }

    }

    @Override
    public UserDTO save(UserDTO user) {

        UserModel userModel = UserModel.builder()
                .userName(user.getUserName())
                .password(user.getPassword())
                .email(user.getEmail())
                .role(user.getRole())
                .telephoneNumber(user.getTelephoneNumber())
                .secondTelephoneNumber(user.getSecondTelephoneNumber())
                .province(user.getProvince())
                .city(user.getCity())
                .address(user.getAddress())
                .postalCode(user.getPostalCode())
                .build();

        return UserMapper.toUserDto(userRepository.save(userModel));
    }

    @Override
    public UserDTO updateUser(UserDTO user, Long id) {
        UserModel userModel = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        userModel.setUserName(user.getUserName());
        userModel.setPassword(user.getPassword());
        userModel.setEmail(user.getEmail());
        userModel.setRole(user.getRole());
        userModel.setTelephoneNumber(user.getTelephoneNumber());
        userModel.setSecondTelephoneNumber(user.getSecondTelephoneNumber());
        userModel.setProvince(user.getProvince());
        userModel.setCity(user.getCity());
        userModel.setAddress(user.getAddress());
        userModel.setPostalCode(user.getPostalCode());

        return UserMapper.toUserDto(userRepository.save(userModel));
    }

    @Override
    public void delete(Long id) {
        UserModel userModel = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        userRepository.delete(userModel);
    }

}
