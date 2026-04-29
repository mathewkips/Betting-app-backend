package com.betting_app.dashboard.user.security;

import com.betting_app.dashboard.admin.service.AdminService;
import com.betting_app.dashboard.user.service.AppUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AppUserDetailsService userService;
    private final AdminService adminService;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   AppUserDetailsService userService,
                                   AdminService adminService) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.adminService = adminService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return "/api/payments/paystack/webhook".equals(request.getServletPath());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        if (!jwtService.isValid(token)) {
            chain.doFilter(request, response);
            return;
        }

        String username = jwtService.extractUsername(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails details = loadUserOrAdmin(username);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            details,
                            null,
                            details.getAuthorities()
                    );

            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        chain.doFilter(request, response);
    }

    private UserDetails loadUserOrAdmin(String username) {
        try {
            return userService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            return adminService.loadUserByUsername(username);
        }
    }
}