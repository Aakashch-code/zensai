package com.example.zensai.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // NEW: Check if the frontend is trying to browse a specific store
        String requestedStoreId = request.getHeader("X-Store-ID");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                UUID userId = jwtUtil.extractUserId(token);
                String role = jwtUtil.extractRole(token);
                String userTenantId = jwtUtil.extractTenantId(token);

                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    if (jwtUtil.isTokenValid(token, userId)) {

                        // MARKETPLACE ROUTING: Customers see everything. Admins see only their store.
                        if ("CUSTOMER".equals(role)) {
                            TenantContext.setCurrentTenant("GLOBAL");
                        } else {
                            TenantContext.setCurrentTenant(userTenantId);
                        }

                        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                userId, null, authorities
                        );
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}