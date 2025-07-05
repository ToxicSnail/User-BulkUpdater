package ru.shift.userimporter.core.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shift.userimporter.api.dto.DetailedFileStatisticDto;
import ru.shift.userimporter.api.dto.FileResponseDto;
import ru.shift.userimporter.api.mapper.FileMapper;
import ru.shift.userimporter.core.model.FileStatus;
import ru.shift.userimporter.core.model.UploadedFile;
import ru.shift.userimporter.core.repository.UploadedFileRepository;

import java.util.List;
@Service
@RequiredArgsConstructor
public class StatisticService {
    private final UploadedFileRepository repo;
    private final FileMapper         mapper;

    @Transactional(readOnly = true)
    public DetailedFileStatisticDto detailed(Integer fileId) {
        UploadedFile file = repo.findById(fileId)
                .orElseThrow(() -> new EntityNotFoundException("Файл не найден"));
        return mapper.toDetailedDto(file);
    }

    @Transactional(readOnly = true)
    public List<FileResponseDto> list(FileStatus status) {
        return (status == null ? repo.findAll() : repo.findByStatus(status))
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }
}