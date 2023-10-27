package com.example.SecurityMicroservice.Services;

import com.example.SecurityMicroservice.DTO.UserDTO;
import com.example.SecurityMicroservice.Models.Role;
import com.example.SecurityMicroservice.Models.User;
import com.example.SecurityMicroservice.Repositories.UserRepository;
import com.example.SecurityMicroservice.Utils.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    @Autowired
    private final UserRepository userRepository;

    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) {
                return userRepository.findById(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            }
        };
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

//List All Users Information
    public List<UserDTO> getAllUsers(){
        return userRepository.findAll().stream()
                .map(b -> UserMapper.mapToDTO(b))
                .collect(Collectors.toList());
    }

//Get All users with pagination
    public Page<User> findUsersWithPagination(int page, int pageSize, String field, String sort) {

        Sort.Direction direction = Sort.Direction.DESC;
        if ("ASC".equalsIgnoreCase(sort)) {
            direction = Sort.Direction.ASC;
        }
        return userRepository.findAll(PageRequest.of(page, pageSize, Sort.by(direction, field)));
    }

//Get User by Id
    public Optional<User> getUserById(String id){
        return userRepository.findById(id);
    }

//Delete User by Id
    public ResponseEntity<String> deleteUserById(String id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            userRepository.deleteById(id);
            return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
        }

        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    }


    //Check if user exists by userName
    public boolean checkUserByUsername(String userName){
        return userRepository.existsByUserName(userName);
    }

    //Check if user exists by email
    public boolean checkUserByEmail(String userName){
        return userRepository.existsByEmail(userName);
    }

}


//----------------------------------------------------------------

//    public User registerUser(String username, String password, Set<Role> roles) {
//        // Create the user
//        User user = new User();
//        user.setUsername(username);
//        user.setPassword(password);
//        user.setRoles(roles); // Assign the roles to the user
//
//        return userRepository.save(user);
//    }








