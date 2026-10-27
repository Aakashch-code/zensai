package com.example.zensai.controller;

import com.example.zensai.dto.RecordDto;
import com.example.zensai.exception.ResourceNotFoundException;
import com.example.zensai.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @GetMapping
    public ResponseEntity<?> getAllRecords(
            @RequestParam(required = false) String search, 
            @ParameterObject Pageable pageable) {
        try {
            Page<RecordDto> records = recordService.getRecords(search, pageable);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching records.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRecordById(@PathVariable Long id) {
        try {
            RecordDto record = recordService.getRecordById(id);
            return ResponseEntity.ok(record);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching the record.");
        }
    }

    @PostMapping
    public ResponseEntity<?> createRecord(@RequestBody RecordDto requestDto) {
        try {
            RecordDto createdRecord = recordService.createRecord(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRecord);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while creating the record.");
        }
    }
}