package com.example.zensai.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.TenantId;

import java.util.UUID;

@Entity
@Data
@Table(name = "record_data")
public class RecordData {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @TenantId
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    private String name;
    private String details;
}