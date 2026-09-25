package com.example.zensai.service;

import com.example.zensai.domain.RecordData;
import com.example.zensai.dto.RecordDto;
import com.example.zensai.repository.RecordDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final RecordDataRepository repository;

    public List<RecordDto> getAllRecords() {

        return repository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public RecordDto createRecord(RecordDto requestDto) {

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