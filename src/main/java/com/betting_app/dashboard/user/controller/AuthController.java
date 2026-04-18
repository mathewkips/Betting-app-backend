
package com.betting_app.dashboard.user.controller;

import com.betting_app.dashboard.user.dto.*;
import com.betting_app.dashboard.user.model.User;
import com.betting_app.dashboard.user.repository.UserRepository;
import com.betting_app.dashboard.user.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,
                          JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        String username = request.getUsername().trim();
        String phone = normalizePhone(request.getPhoneNumber());

        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.badRequest().body(
                    new AuthResponse(false, "Username already taken", null, null, false)
            );
        }

        if (userRepository.existsByPhoneNumber(phone)) {
            return ResponseEntity.badRequest().body(
                    new AuthResponse(false, "Phone number already registered", null, null, false)
            );
        }

        User user = new User();
        user.setUsername(username);
        user.setPhoneNumber(phone);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPremium(false);
        user.setEnabled(true);

        userRepository.save(user);

        String token = jwtService.generateToken(user.getUsername());

        return ResponseEntity.ok(
                new AuthResponse(true, "Signup successful", token, user.getUsername(), user.isPremium())
        );
    }
//
//    @PostMapping("/login")
//    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        request.getUsername(),
//                        request.getPassword()
//                )
//        );
//
//        User user = userRepository.findByUsername(request.getUsername())
//                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
//
//        String token = jwtService.generateToken(user.getUsername());
//
//        return ResponseEntity.ok(
//                new AuthResponse(true, "Login successful", token, user.getUsername(), user.isPremium())
//        );
//    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

            String token = jwtService.generateToken(user.getUsername());

            return ResponseEntity.ok(
                    new AuthResponse(true, "Login successful", token, user.getUsername(), user.isPremium())
            );
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body(
                    new AuthResponse(false, "Invalid credentials", null, null, false)
            );
        }
    }

    private String normalizePhone(String phone) {
        String cleaned = phone.replaceAll("\\s+", "");
        if (cleaned.startsWith("0")) {
            return "254" + cleaned.substring(1);
        }
        if (cleaned.startsWith("+254")) {
            return cleaned.substring(1);
        }
        return cleaned;
    }
}