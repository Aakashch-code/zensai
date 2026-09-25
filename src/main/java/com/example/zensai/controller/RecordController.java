package com.example.zensai.controller;

import com.example.zensai.domain.RecordData;
import com.example.zensai.dto.RecordDto;
import com.example.zensai.repository.RecordDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordDataRepository repository;

    @GetMapping
    public ResponseEntity<List<RecordDto>> getAllRecords() {
        List<RecordDto> dtos = repository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<RecordDto> createRecord(@RequestBody RecordDto requestDto) {

        RecordData entity = new RecordData();
        entity.setName(requestDto.getName());
        entity.setDetails(requestDto.getDetails());

        RecordData savedEntity = repository.save(entity);

        return ResponseEntity.ok(mapToDto(savedEntity));
    }

    private RecordDto mapToDto(RecordData entity) {
        RecordDto dto = new RecordDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDetails(entity.getDetails());
        return dto;
    }

}