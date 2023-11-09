package com.example.SecurityMicroservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String userName;
    private String creationDate;
    private String lastLogin;
    private String lastUpdate;
    private String active;

}
