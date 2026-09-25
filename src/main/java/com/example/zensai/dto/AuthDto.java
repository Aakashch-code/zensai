package com.example.zensai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.zensai.domain.UsersRole;

public class AuthDto {

    @Data
    public static class RegisterRequest {
        private String username;
        private String email;
        private String password;
        private String workspaceName;
        private String inviteCode;
        private UsersRole role;
    }

    @Data
    public static class LoginRequest {
        private String login;
        private String password;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthResponse {
        private String token;
    }
}