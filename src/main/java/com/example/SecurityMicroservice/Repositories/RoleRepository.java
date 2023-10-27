package com.example.SecurityMicroservice.Repositories;

import com.example.SecurityMicroservice.Models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

        Optional<Role> findByName(String name);
}