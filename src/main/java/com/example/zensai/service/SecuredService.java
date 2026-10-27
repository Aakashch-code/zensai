package com.example.zensai.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public abstract class SecuredService {

    protected UUID currentUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return (UUID) authentication.getPrincipal();
    }
}