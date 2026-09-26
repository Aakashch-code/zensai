package com.example.zensai.repository;

import com.example.zensai.domain.RecordData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RecordDataRepository extends JpaRepository<RecordData, UUID> {

    Page<RecordData> findByNameContainingIgnoreCase(String name, Pageable pageable);
}