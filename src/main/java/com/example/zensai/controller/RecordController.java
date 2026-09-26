package com.example.zensai.controller;

import com.example.zensai.dto.RecordDto;
import com.example.zensai.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @GetMapping
    public ResponseEntity<Page<RecordDto>> getAllRecords(@RequestParam(required = false) String name, @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(recordService.getRecords(name, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecordDto> getRecordById(@PathVariable UUID id) {
        return ResponseEntity.ok(recordService.getRecordById(id));
    }

    @PostMapping
    public ResponseEntity<RecordDto> createRecord(@RequestBody RecordDto requestDto) {
        return ResponseEntity.ok(recordService.createRecord(requestDto));
    }
}