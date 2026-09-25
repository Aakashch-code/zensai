package com.example.zensai.repository;

import com.example.zensai.domain.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {
    Optional<Workspace> findByInviteCode(String inviteCode);
}