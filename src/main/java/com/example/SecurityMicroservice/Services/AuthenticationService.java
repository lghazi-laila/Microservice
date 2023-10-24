package com.example.SecurityMicroservice.Services;

import com.example.SecurityMicroservice.DTO.JwtAuthenticationResponse;
import com.example.SecurityMicroservice.Models.Role;
import com.example.SecurityMicroservice.Models.User;
import com.example.SecurityMicroservice.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.SecurityMicroservice.DTO.SignUpRequest;
import com.example.SecurityMicroservice.DTO.SignInRequest;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationResponse signup(SignUpRequest request) {
        if(userService.checkUserByUsername(request.getUserName())){
            throw new IllegalArgumentException("Username is already taken.");
        }
        if(userService.checkUserByEmail(request.getEmail())){
            throw new IllegalArgumentException("Email is already taken.");
        }
        var user = User
                .builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .userName(request.getUserName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .active(true) //to check
                .build();

        user = userService.save(user);
        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }


    public JwtAuthenticationResponse login(SignInRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        }catch (Exception e){
            throw new IllegalArgumentException("Invalid email or password.");
        }


        //find user by email
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        //check if user is active
        if(!user.getActive()){
            throw new IllegalArgumentException("User is not Active. ");
        }

        //update last login data
        var updatedUser = userService.updateLastLogin(user);
        var jwt = jwtService.generateToken(updatedUser);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

    //Add user
    public JwtAuthenticationResponse addUser(User user){
        if(userService.checkUserByUsername(user.getUsername())){
            throw new IllegalArgumentException("Username is already taken.");
        }
        if(userService.checkUserByEmail(user.getEmail())){
            throw new IllegalArgumentException("Email is already taken.");
        }
        var newUser = User
                .builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .userName(user.getUsername())
                .password(passwordEncoder.encode(user.getPassword()))
                .role(Role.ROLE_USER)
                .active(true)
                .build();

        user = userService.save(newUser);
        var jwt = jwtService.generateToken(newUser);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }
}