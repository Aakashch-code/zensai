package com.example.zensai.controller;

import com.example.zensai.dto.CustomerProfileDto;
import com.example.zensai.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers/profile")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<CustomerProfileDto> getProfile() {
        return ResponseEntity.ok(customerService.getProfile());
    }

    @PutMapping
    public ResponseEntity<CustomerProfileDto> updateProfile(
            @RequestBody CustomerProfileDto request) {

        return ResponseEntity.ok(
                customerService.updateProfile(request)
        );
    }
}