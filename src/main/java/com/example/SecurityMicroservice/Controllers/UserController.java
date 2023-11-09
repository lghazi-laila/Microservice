package com.example.SecurityMicroservice.Controllers;

import com.example.SecurityMicroservice.DTO.UserDTO;
import com.example.SecurityMicroservice.Models.User;
import com.example.SecurityMicroservice.Response.ResponseHandler;
import com.example.SecurityMicroservice.Services.UserService;
import com.example.SecurityMicroservice.Utils.UserMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/users")
@AllArgsConstructor
@NoArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    //Get All Users
    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers()
    {
        List<UserDTO> users = userService.getAllUsers();
        if(users.isEmpty()){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(users, HttpStatus.OK);
    }

    //Get all Users with Sort and Pagination
    @GetMapping("/test")
    @PreAuthorize("hasAnyAuthority('MANAGER','ADMIN')")
    public ResponseEntity<?> getCustomersByPageWithSortAndField(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "field", defaultValue = "id") String field,
            @RequestParam(name = "sort", defaultValue = "DESC") String sort
    ) {
        Page<UserDTO> users = userService.findUsersWithPagination(page, pageSize, field, sort);
        //return new ResponseEntity<>(users, HttpStatus.OK);
        return ResponseHandler.generateResponse(null,HttpStatus.OK,users);
    }//Search for access denied handler

    //Retrieve all the users list, with a limit of 10 users per page.
    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('MANAGER','ADMIN')")
    public ResponseEntity<?> getCustomersByPageWithSort (
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sort", defaultValue = "DESC") String sort
    ) {
        Page<UserDTO> users = userService.findUsersWithPagination(page, 10, "email", sort);

        return ResponseHandler.generateResponse("OK",HttpStatus.OK,users);
    }

    //Get User with Id
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MANAGER','ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable String id){
        Optional<User> user = userService.getUserById(id);
        if(user.isPresent()){
            return  ResponseHandler.generateResponse("OK",HttpStatus.OK ,UserMapper.mapToDTO(user.get()));
        }
        return  ResponseHandler.generateResponse("User not found",HttpStatus.NOT_FOUND ,null);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('MANAGER','ADMIN')")
    public ResponseEntity<?> getUsersByQueryByPageWithSort(
            @RequestParam(name = "query") String query,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "field", required = false) String field,
            @RequestParam(name = "sort", defaultValue = "ASC") String sort
    ) {
        if (!"ASC".equalsIgnoreCase(sort) && !"DESC".equalsIgnoreCase(sort)) {
            return ResponseEntity.badRequest().build();
        }

        Page<User> users = userService.searchUser(query, page, pageSize, field, Sort.Direction.valueOf(sort));
        List<UserDTO> userDtoList = users.getContent().stream()
                .map(user -> UserMapper.mapToDTO(user))
                .collect(Collectors.toList());

        PageImpl<UserDTO> userDTOS = new PageImpl<>(userDtoList, users.getPageable(), users.getTotalElements());

        return  ResponseHandler.generateResponse("OK",HttpStatus.OK ,userDTOS);

    }

    //Update User
    @PostMapping ("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateCustomer(
            @PathVariable String id,
            @RequestBody User updatedUser) {
        try{
            User updated = userService.updateCustomer(id, updatedUser);
            return ResponseHandler.generateResponse("user updated successfully",HttpStatus.OK,UserMapper.mapToDTO(updated));
        }catch (Exception e){
            return ResponseHandler.generateResponse(e.getMessage(),HttpStatus.BAD_REQUEST, null);
        }
    }

    //Delete User by Id
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteUserById(@PathVariable String id){
        return userService.deleteUserById(id);
    }

    //DeActivate User
    @PostMapping("/SD/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> softDeleteUserById(@PathVariable String id){
        return userService.softDeleteUserById(id);
    }


}
