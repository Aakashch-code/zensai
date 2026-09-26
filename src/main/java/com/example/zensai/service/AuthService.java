package com.example.zensai.service;

import com.example.zensai.domain.Users;
import com.example.zensai.domain.UsersRole;
import com.example.zensai.domain.Workspace;
import com.example.zensai.dto.AuthDto.*;
import com.example.zensai.exception.DuplicateResourceException;
import com.example.zensai.exception.ResourceNotFoundException;
import com.example.zensai.repository.UserRepository;
import com.example.zensai.repository.WorkspaceRepository;
import com.example.zensai.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final WorkspaceRepository workspaceRepo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    @Transactional
    public String register(RegisterRequest request) {

        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (request.getRole() == null) {
            throw new IllegalArgumentException("User role must be specified");
        }

        if (userRepo.existsByUsername(request.getUsername()) || userRepo.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Username or Email is already in use");
        }

        Workspace workspace;

        if (request.getRole() == UsersRole.ORGANIZER) {
            if (request.getWorkspaceName() == null || request.getWorkspaceName().trim().isEmpty()) {
                throw new IllegalArgumentException("Workspace name is required for ORGANIZER role");
            }

            workspace = new Workspace();
            workspace.setName(request.getWorkspaceName());
            workspace.setInviteCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            workspaceRepo.save(workspace);

        } else {
            if (request.getInviteCode() == null || request.getInviteCode().trim().isEmpty()) {
                throw new IllegalArgumentException("Invite code is required to join an existing workspace");
            }

            workspace = workspaceRepo.findByInviteCode(request.getInviteCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Invalid invite code provided"));
        }

        Users user = new Users(
                null, workspace, request.getUsername(), request.getEmail(),
                encoder.encode(request.getPassword()), request.getRole()
        );
        userRepo.save(user);

        return request.getRole() == UsersRole.ORGANIZER
                ? "Registered! Invite code: " + workspace.getInviteCode()
                : "Registered successfully";
    }

    public AuthResponse login(LoginRequest request) {

        if (request.getLogin() == null || request.getLogin().trim().isEmpty()) {
            throw new IllegalArgumentException("Username/Email cannot be empty");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));

        Users user = userRepo.findByUsernameOrEmail(request.getLogin())
                .orElseThrow(() -> new ResourceNotFoundException("User not found in the system"));

        String workspaceId = user.getWorkspace().getId().toString();
        String token = jwtUtil.generateToken(user.getId(), user.getRole().name(), workspaceId);

        return new AuthResponse(token);
    }
}