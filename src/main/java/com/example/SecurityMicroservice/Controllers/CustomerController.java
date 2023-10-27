package com.example.SecurityMicroservice.Controllers;


import com.example.SecurityMicroservice.DTO.Customer;
import com.example.SecurityMicroservice.DTO.JwtAuthenticationResponse;
import com.example.SecurityMicroservice.Models.User;
import com.example.SecurityMicroservice.Services.AuthenticationService;
import com.example.SecurityMicroservice.Services.CustomerService;
import com.example.SecurityMicroservice.Utils.CustomerMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@RestController
@RequestMapping("/v1/customers")
public class CustomerController {

    @Autowired
    private final CustomerService customerService;

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<User> users = customerService.getAllCustomers();
        List<Customer> customer = users.stream()
                .map(c -> {
                    CustomerMapper customerProfile = new CustomerMapper();
                    return customerProfile.mapUserToCustomer(c);
                })
                .collect(Collectors.toList());

        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable String customerId) {
        User user = customerService.getCustomerById(customerId);

        if (user != null) {
            CustomerMapper customerProfile = new CustomerMapper();
            Customer customer = customerProfile.mapUserToCustomer(user);
            return ResponseEntity.ok(customer);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

//    @GetMapping("/firstname/{customerFirstName}")
//    public ResponseEntity<List<Customer>> getCustomerByFirstName(@PathVariable String customerFirstName) {
//        List<User> users = customerService.getCustomerByFirstName(customerFirstName);
//        List<Customer> customer = users.stream()
//                .map(c -> {
//                    CustomerMapper customerProfile = new CustomerMapper();
//                    return customerProfile.mapUserToCustomer(c);
//                })
//                .toList();
//        return new ResponseEntity<>(customer, HttpStatus.OK);
//    }

//    @GetMapping("/byQuery")
//    public ResponseEntity<List<Customer>> getCustomersByFirstNameQuery(@RequestParam("query") String query) {
//        List<User> users = customerService.findCustomersByFirstName(query);
//
//        if (users.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        List<Customer> customers = users.stream()
//                .map(user -> new CustomerMapper().mapUserToCustomer(user))
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(customers);
//    }


    //----------------------------

//    @GetMapping("/search")
//    public ResponseEntity<List<Customer>> searchCustomers(@RequestParam("query") String query) {
//        List<User> users = customerService.searchCustomers(query);
//
//        if (users.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        List<Customer> customers = users.stream()
//                .map(user -> new CustomerMapper().mapUserToCustomer(user))
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(customers);
//    }


    //----------------------------

    @GetMapping("/byField")
    public ResponseEntity<List<Customer>> getCustomersByField(@RequestParam("field") String field) {
        List<User> users = customerService.findCustomersWithSorting(field);
        //List<Customer> customers = customerService.findCustomersWithSorting(field.toLowerCase(Locale.ROOT));
        List<Customer> customers = users.stream()
                .map(user -> new CustomerMapper().mapUserToCustomer(user))
                .collect(Collectors.toList());
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }


    @GetMapping("/byPage")
    public ResponseEntity<Page<Customer>> getCustomersByPage(
            @RequestParam("offset") int offset,
            @RequestParam("pageSize") int pageSize
    ) {
        Page<User> users = customerService.findCustomersWithPagination(offset, pageSize);

        if (users.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Page<Customer> customers = users.map(user -> new CustomerMapper().mapUserToCustomer(user));

        return new ResponseEntity<>(customers, HttpStatus.OK);
    }


    @GetMapping("byPageWithField")
    public ResponseEntity<Page<Customer>> getCustomersByPageAndField(
            @RequestParam("offset") int offset,
            @RequestParam("pageSize") int pageSize,
            @RequestParam("field") String field
    ) {
        Page<User> users = customerService.findCustomersWithPagination(offset, pageSize, field);

        if (users.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Page<Customer> customers = users.map(user -> new CustomerMapper().mapUserToCustomer(user));
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    @GetMapping("/byPageWithSort")
    public ResponseEntity<Page<Customer>> getCustomersByPageWithSort(
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "field", required = false) String field,
            @RequestParam(name = "sort", defaultValue = "ASC") String sort
    ) {
        if (!"ASC".equalsIgnoreCase(sort) && !"DESC".equalsIgnoreCase(sort)) {
            return ResponseEntity.badRequest().build();
        }

        Page<User> users = customerService.findCustomersWithPagination(offset, pageSize, field, sort);
        Page<Customer> customers = users.map(user -> new CustomerMapper().mapUserToCustomer(user));

        return new ResponseEntity<>(customers, HttpStatus.OK);
    }


    //----------------------------------------------------------------

//    @PostMapping
//    public ResponseEntity<?> createCustomer(@RequestBody User user) {
//        if (!customerService.isEmailUnique(user.getEmail())) {
//            String errorMessage = "Email is not unique. Email already exists";
//            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
//        }
//
//        User createdUser = customerService.createCustomer(user);
//
//        if (createdUser != null) {
//            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
//        } else {
//            String errorMessage = "Failed to create customer.";
//            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @Autowired
    private final AuthenticationService authenticationService;
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<JwtAuthenticationResponse> addUser (@RequestBody User user){
        try{
            JwtAuthenticationResponse jwtAuthenticationResponse = authenticationService.addCustomer(user);
            return new ResponseEntity<>(jwtAuthenticationResponse, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }


    //----------------------------------------------------------------

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String customerId) {
        customerService.deleteCustomer(customerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{customerId}") // soft delete customer
    public ResponseEntity<Void> deactivateCustomer(@PathVariable String customerId) {
        User user = customerService.getCustomerById(customerId);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        user.setActive(false);
        customerService.createCustomer(user);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //----------------------------------------------------------------

    @PutMapping("/{customerId}")
    public ResponseEntity<User> updateCustomer(@PathVariable String customerId, @RequestBody User updatedUser) {
        User updated = customerService.updateCustomer(customerId, updatedUser);
        if (updated != null) {
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/validate/{customerId}") // validate email
    public ResponseEntity<Void> validateCustomer(@PathVariable String customerId) {
        User user = customerService.getCustomerById(customerId);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        if(user.isValidAccount()){
            return ResponseEntity.badRequest().build();
        }

        user.setValidAccount(true);
        customerService.createCustomer(user);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //----------------------------------------------------------------

    @PatchMapping(path = "/profile/update/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<User> updateCustomer(
            @PathVariable String id,
            @RequestBody JsonPatch patch) throws JsonPatchException, JsonProcessingException {

            User user = customerService.getCustomerById(id);
            User userPatched = customerService.applyPatchToCustomer(patch, user);
            return ResponseEntity.ok(userPatched);
    }

}
