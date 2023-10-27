package com.example.SecurityMicroservice.Controllers;

import com.example.SecurityMicroservice.Models.Role;
import com.example.SecurityMicroservice.Services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

public class RoleController {
    @Autowired
    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/add")
    public ResponseEntity<Role> addRole(@RequestParam String name) {
        Role role = roleService.createRole(name);
        return ResponseEntity.ok(role);
    }

    @DeleteMapping("/delete/{roleId}")
    public ResponseEntity<String> deleteRole(@PathVariable Long roleId) {
        roleService.deleteRoleById(roleId);
        return ResponseEntity.ok("Role deleted successfully");
    }
}
