package com.example.SecurityMicroservice.Controllers;

import com.example.SecurityMicroservice.DTO.JwtAuthenticationResponse;
import com.example.SecurityMicroservice.DTO.SignInRequest;
import com.example.SecurityMicroservice.DTO.SignUpRequest;
import com.example.SecurityMicroservice.Models.User;
import com.example.SecurityMicroservice.Services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public JwtAuthenticationResponse signup(@RequestBody SignUpRequest request) {
        return authenticationService.signup(request);
    }

    @PostMapping("/login")
    public JwtAuthenticationResponse signin(@RequestBody SignInRequest request) {
        return authenticationService.signin(request);
    }

    //Add a user
    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<JwtAuthenticationResponse> addUser (@RequestBody User user){
        JwtAuthenticationResponse jwtAuthenticationResponse = authenticationService.addUser(user);
        return new ResponseEntity(jwtAuthenticationResponse, HttpStatus.OK);
    }

}