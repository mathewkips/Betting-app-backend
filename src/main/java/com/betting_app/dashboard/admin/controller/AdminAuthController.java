package com.betting_app.dashboard.admin.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    @GetMapping("/me")
    public Map<String, Object> me(Principal principal) {
        return Map.of(
                "success", true,
                "email", principal.getName()
        );
    }
}
