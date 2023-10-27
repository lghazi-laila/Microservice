package com.example.SecurityMicroservice.DTO;

import com.example.SecurityMicroservice.Models.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String userName;
    private String password;
    //private Set<Role> role;
}
