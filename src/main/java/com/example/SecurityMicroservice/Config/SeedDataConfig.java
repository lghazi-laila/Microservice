package com.example.SecurityMicroservice.Config;

import com.example.SecurityMicroservice.Models.User;
import com.example.SecurityMicroservice.Models.Role;
import com.example.SecurityMicroservice.Repositories.UserRepository;
import com.example.SecurityMicroservice.Services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeedDataConfig implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {

        if (userRepository.count() == 0) {

            // Create a Set<Role> to store the roles
            Set<Role> roles = new HashSet<>();
            roles.add(new Role("ADMIN"));
            roles.add(new Role("USER"));
            roles.add(new Role("CUSTOMER"));

            

            User admin = User
                    .builder()
                    .firstName("admin")
                    .lastName("admin")
                    .userName("admin")
                    .email("admin@admin.com")
                    .password(passwordEncoder.encode("password"))
                    .role(roles)
                    .validAccount(true)
                    .active(true)
                    .build();

            userService.save(admin);
            log.debug("created ADMIN user - {}", admin);
        }
    }


}