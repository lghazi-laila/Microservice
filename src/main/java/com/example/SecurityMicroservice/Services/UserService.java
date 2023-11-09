package com.example.SecurityMicroservice.Services;

import com.example.SecurityMicroservice.DTO.UserDTO;
import com.example.SecurityMicroservice.Models.User;
import com.example.SecurityMicroservice.Repositories.UserRepository;
import com.example.SecurityMicroservice.Response.ResponseHandler;
import com.example.SecurityMicroservice.Utils.UserMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) {
                return userRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            }
        };
    }

    public User loadUserById(String Id){
        return userRepository.findById(Id).orElseThrow(()-> new UsernameNotFoundException("User not found"));

    }

    public User save(User newUser) {
        if (newUser.getId() == null) {
            newUser.setCreationDate(LocalDateTime.now());
        }
        newUser.setLastUpdate(LocalDateTime.now());
        newUser.setLastLogin(LocalDateTime.now());
        return userRepository.save(newUser);
    }

    public User updateLastLogin(User user){
        user.setLastLogin(LocalDateTime.now());
        return userRepository.save(user);
    }

    public boolean checkUserByUsername(String userName){
        return userRepository.existsByUserName(userName);
    }

    public boolean checkUserByEmail(String userName){
        return userRepository.existsByEmail(userName);
    }


    //List All Users Information
    public List<UserDTO> getAllUsers(){
        return userRepository.findAll().stream()
                .map(b -> UserMapper.mapToDTO(b))
                .collect(Collectors.toList());
    }

    //Get All users with pagination
    public Page<UserDTO> findUsersWithPagination(int page, int pageSize, String field, String sort) {

        Sort.Direction direction = Sort.Direction.DESC;
        if ("ASC".equalsIgnoreCase(sort)) {
            direction = Sort.Direction.ASC;
        }
        //return userRepository.findAll(PageRequest.of(page, pageSize, Sort.by(direction, field)));

        Page<User> userPage = userRepository.findAll(PageRequest.of(page, pageSize, Sort.by(direction, field)));

        List<UserDTO> userDtoList = userPage.getContent().stream()
                .map(user -> UserMapper.mapToDTO(user))
                .collect(Collectors.toList());

        return new PageImpl<>(userDtoList, userPage.getPageable(), userPage.getTotalElements());

    }

    //Get User by Id
    public Optional<User> getUserById(String id){
        return userRepository.findById(id);
    }

    //Search for a user with Id
    public Page<User> searchUser(String query, int page, int pageSize, String field, Sort.Direction direction) {
        String jpqlQuery = "SELECT u FROM User u " +
                "WHERE u.firstName LIKE :searchQuery " +
                "OR u.lastName LIKE :searchQuery " +
                "OR u.email LIKE :searchQuery ";

        // Add the ORDER BY clause based on the field and direction
        if (field != null && direction != null) {
            jpqlQuery += "ORDER BY u." + field + " " + (direction == Sort.Direction.ASC ? "ASC" : "DESC");
        }

        TypedQuery<User> jpqlTypedQuery = entityManager.createQuery(jpqlQuery, User.class)
                .setParameter("searchQuery", "%" + query + "%");

        // Execute the query and get the result list
        List<User> users = jpqlTypedQuery
                .setFirstResult(page)
                .setMaxResults(pageSize)
                .getResultList();

        // Create a Page object
        long totalUsers = users.size(); // You may need to count total users differently
        Pageable pageable = PageRequest.of(page, pageSize);
        return new PageImpl<>(users, pageable, totalUsers);
    }

    //Update customer
    public User updateCustomer(String userId, User updatedUser) throws Exception {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            // Check if the username is being updated to a unique value
            if (!user.getUsername().
                    equals(updatedUser.getUsername()) && userRepository.findByUserName(updatedUser.getUsername()).isPresent()) {
                throw new IllegalArgumentException("Username is not unique");
            }

            // Check if the email is being updated to a unique value
            if (!user.getEmail().
                    equals(updatedUser.getEmail()) && userRepository.findByEmail(updatedUser.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email is not unique");
            }

            // Update the user fields
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
            user.setUserName(updatedUser.getUserName());
            user.setEmail(updatedUser.getEmail());
            user.setRoles(updatedUser.getRoles());
            user.setActive(updatedUser.getActive());

            return userRepository.save(user);
        } else {
            throw new UsernameNotFoundException("Invalid user id");
        }
    }//CHECK IF INPUT FIELDS ARE NULL

    //Delete User by Id
    public ResponseEntity<?> deleteUserById(String id){
        Optional<User> user = userRepository.findById(id);

        if(user.isPresent()){
            userRepository.deleteById(id);
            return ResponseHandler.generateResponse("user deleted successfully", HttpStatus.OK, null);
        }
        return ResponseHandler.generateResponse("User Not Founf", HttpStatus.NOT_FOUND, null);
    }

    //Soft Delete User by Id
    public ResponseEntity<?> softDeleteUserById(String id){
        Optional<User> user = userRepository.findById(id);

        if(user.isPresent()){
            User modifiedUser = user.get();
            modifiedUser.setActive(false);
            userRepository.save(modifiedUser);
            return ResponseHandler.generateResponse("user deleted successfully", HttpStatus.OK, UserMapper.mapToDTO(modifiedUser));
        }
        return ResponseHandler.generateResponse("User Not Found", HttpStatus.NOT_FOUND, null);
    }
}
