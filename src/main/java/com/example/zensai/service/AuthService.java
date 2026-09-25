package com.example.zensai.service;

import com.example.zensai.domain.Users;
import com.example.zensai.domain.UsersRole;
import com.example.zensai.domain.Workspace;
import com.example.zensai.dto.AuthDto.*;
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
        if (userRepo.existsByUsername(request.getUsername()) || userRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Credentials already used");
        }

        Workspace workspace;

        if (request.getRole() == UsersRole.ORGANIZER) {
            workspace = new Workspace();
            workspace.setName(request.getWorkspaceName());
            workspace.setInviteCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            workspaceRepo.save(workspace);
        } else {
            workspace = workspaceRepo.findByInviteCode(request.getInviteCode())
                    .orElseThrow(() -> new RuntimeException("Invalid invite code"));
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
        authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));

        Users user = userRepo.findByUsernameOrEmail(request.getLogin())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String workspaceId = user.getWorkspace().getId().toString();
        String token = jwtUtil.generateToken(user.getId(), user.getRole().name(), workspaceId);

        return new AuthResponse(token);
    }
}