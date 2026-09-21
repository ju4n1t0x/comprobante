package org.ignia.comprobante.security;

import lombok.extern.slf4j.Slf4j;
import org.ignia.comprobante.user.Role;
import org.ignia.comprobante.user.UserModel;
import org.ignia.comprobante.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0){
            UserModel admin = UserModel.builder()
                    .userName("administrador")
                    .password(passwordEncoder.encode("Ch4mpa1803*"))
                    .email("admin@juansasia.com")
                    .role(Role.SUPER_ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("=== Usuario SUPER_ADMIN creado ===");
            log.info("Email: " + admin.getEmail());
            log.info("Password: Ch4mpa1803*");
            log.info("Role: SUPER_ADMIN");
        }

    }
}
