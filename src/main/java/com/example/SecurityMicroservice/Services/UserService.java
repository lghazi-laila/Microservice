package com.example.SecurityMicroservice.Services;

import com.example.SecurityMicroservice.DTO.JwtAuthenticationResponse;
import com.example.SecurityMicroservice.DTO.UserDTO;
import com.example.SecurityMicroservice.Models.Role;
import com.example.SecurityMicroservice.Models.User;
import com.example.SecurityMicroservice.Repositories.UserRepository;
import com.example.SecurityMicroservice.Utils.UserMapper;
import lombok.RequiredArgsConstructor;
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
    private final UserRepository userRepository;

    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) {
                return userRepository.findByEmail(username)
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
    public Page<User> findUsersWithPagination(int offset, int page, String field, String sort) {

        Sort.Direction direction = Sort.Direction.DESC;
        if ("ASC".equalsIgnoreCase(sort)) {
            direction = Sort.Direction.ASC;
        }
        return userRepository.findAll(PageRequest.of(offset, page, Sort.by(direction, field)));
    }

//Get User by Id
    public Optional<User> getUserById(String id){
        return userRepository.findById(id);
    }

//Delete User by Id
    public ResponseEntity deleteUserById(String id){
        Optional<User> user = userRepository.findById(id);

        if(user.isPresent()){
            userRepository.deleteById(id);
            return new ResponseEntity(HttpStatus.OK);
        }

        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

}
