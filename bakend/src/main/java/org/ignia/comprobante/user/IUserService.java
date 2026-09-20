package org.ignia.comprobante.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserService {

    List<UserDTO> findAll();

    Page<UserDTO> page(Pageable pageable);

    Page<UserDTO> page(String search, Pageable pageable);

    UserDTO save(UserDTO user);

    UserDTO updateUser(UserDTO user, Long id);

    void delete(Long id);


}
