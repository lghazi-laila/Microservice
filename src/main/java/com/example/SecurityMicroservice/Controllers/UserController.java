package com.example.SecurityMicroservice.Controllers;

import com.example.SecurityMicroservice.DTO.UserDTO;
import com.example.SecurityMicroservice.Models.User;
import com.example.SecurityMicroservice.Services.UserService;
import com.example.SecurityMicroservice.Utils.UserMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/users")
@AllArgsConstructor
@NoArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/anon")
    public String anonEndPoint() {
        return "everyone can see this";
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('USER')")
    public String usersEndPoint() {
        return "ONLY users can see this";
    }

    @GetMapping("/admins")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminsEndPoint() {
        return "ONLY admins can see this";
    }



//Get All Users
    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers()
    {
        List<UserDTO> users = userService.getAllUsers();
        if(users.isEmpty()){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

//Get all Users with Sort and Pagination
    @GetMapping("")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Page<User>> getCustomersByPageWithSort(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "field", defaultValue = "id") String field,
            @RequestParam(name = "sort", defaultValue = "DESC") String sort
    ) {
        Page<User> users = userService.findUsersWithPagination(page, pageSize, field, sort);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }//Search for access denied handler

//Get User with Id
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String id){
        Optional<User> user = userService.getUserById(id);
        if(user.isPresent()){
            return new ResponseEntity<>(UserMapper.mapToDTO(user.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }//to do : forbidden status

//Delete User by Id
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUserById(@PathVariable String id){
        return userService.deleteUserById(id);
    }

}

//----------------------------------------------------------------





//---------------------------------------------------------------

//    @PostMapping("/{userId}/addRole")
//    public ResponseEntity<User> addRoleToUser(@PathVariable Long userId, @RequestParam String roleName) {
//        Optional<User> userOptional = userService.getUserById(userId);
//        Optional<Role> roleOptional = roleService.getRoleByName(roleName);
//
//        if (userOptional.isPresent() && roleOptional.isPresent()) {
//            User user = userOptional.get();
//            Role role = roleOptional.get();
//
//            // Add the role to the user
//            user.addRole(role);
//            userService.saveUser(user);
//
//            return ResponseEntity.ok(user);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

//-----------------------------------------------------------


//    @DeleteMapping("/{userId}/removeRole")
//    public ResponseEntity<User> removeRoleFromUser(@PathVariable Long userId, @RequestParam String roleName) {
//        Optional<User> userOptional = userService.getUserById(userId);
//        Optional<Role> roleOptional = roleService.getRoleByName(roleName);
//
//        if (userOptional.isPresent() && roleOptional.isPresent()) {
//            User user = userOptional.get();
//            Role role = roleOptional.get();
//
//            // Remove the role from the user
//            user.removeRole(role);
//            userService.saveUser(user);
//
//            return ResponseEntity.ok(user);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }









