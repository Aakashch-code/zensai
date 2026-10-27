package com.example.zensai.service;

import com.example.zensai.domain.RecordData;
import com.example.zensai.dto.RecordDto;
import com.example.zensai.exception.ResourceNotFoundException;
import com.example.zensai.repository.RecordDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final RecordDataRepository repository;

    public Page<RecordDto> getRecords(String searchFilter, Pageable pageable) {
        Page<RecordData> recordPage;

        if (searchFilter != null && !searchFilter.trim().isEmpty()) {
            recordPage = repository.findByNameContainingIgnoreCaseOrDetailsContainingIgnoreCase(
                    searchFilter, searchFilter, pageable);
        } else {
            recordPage = repository.findAll(pageable);
        }

        return recordPage.map(this::mapToDto);
    }

    public RecordDto getRecordById(Long id) {
        return repository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with ID: " + id));
    }

    public RecordDto createRecord(RecordDto requestDto) {

        if (requestDto.getName() == null || requestDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Record name cannot be null or empty");
        }

        if (requestDto.getDetails() == null || requestDto.getDetails().trim().isEmpty()) {
            throw new IllegalArgumentException("Record details cannot be null or empty");
        }

        RecordData entity = new RecordData();
        entity.setName(requestDto.getName());
        entity.setDetails(requestDto.getDetails());

        RecordData savedEntity = repository.save(entity);
        return mapToDto(savedEntity);
    }

    private RecordDto mapToDto(RecordData entity) {
        RecordDto dto = new RecordDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDetails(entity.getDetails());
        return dto;
    }
}