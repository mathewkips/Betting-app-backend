
package com.betting_app.dashboard.admin.controller;

import com.betting_app.dashboard.admin.dto.AdminLoginRequest;
import com.betting_app.dashboard.admin.dto.AdminLoginResponse;
import com.betting_app.dashboard.user.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AdminAuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }
    

    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponse> login(
            @Valid @RequestBody AdminLoginRequest request
    ) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );

            String token = jwtService.generateToken(authentication.getName());
            return ResponseEntity.ok(
                    new AdminLoginResponse(
                            true,
                            "Login successful",
                            token,
                            request.email()
                    )
            );
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body(
                    new AdminLoginResponse(
                            false,
                            "Invalid email or password",
                            null,
                            null
                    )
            );
        }
    }
    
}