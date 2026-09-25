package com.example.zensai.controller;

import com.example.zensai.dto.RecordDto;
import com.example.zensai.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @GetMapping
    public ResponseEntity<List<RecordDto>> getAllRecords() {
        return ResponseEntity.ok(recordService.getAllRecords());
    }

    @PostMapping
    public ResponseEntity<RecordDto> createRecord(
            @RequestBody RecordDto requestDto) {

        return ResponseEntity.ok(recordService.createRecord(requestDto));
    }
}