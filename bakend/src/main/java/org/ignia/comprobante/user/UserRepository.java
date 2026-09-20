package org.ignia.comprobante.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<UserModel, Long> {

    @Query("SELECT u FROM UserModel u WHERE u.userName LIKE %:search% OR u.email LIKE %:search%")
    Page<UserModel> findByUsernameContainingOrEmailContaining(String username, String email, Pageable pageable);
}
