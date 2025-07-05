package ru.shift.userimporter.core.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shift.userimporter.api.dto.DetailedFileStatisticDto;
import ru.shift.userimporter.api.dto.FileResponseDto;
import ru.shift.userimporter.api.mapper.FileMapper;
import ru.shift.userimporter.core.model.FileStatus;
import java.util.List;
import ru.shift.userimporter.core.repository.FileMetaRepository;

@Service
@RequiredArgsConstructor
public class StatisticService {
    private final FileMetaRepository repo;
    private final FileMapper mapper;

    @Transactional(readOnly = true)
    public DetailedFileStatisticDto detailed(Integer fileId) {
        return repo.findById(fileId)
                .map(mapper::toDetailedDto)
                .orElseThrow(() -> new EntityNotFoundException("Файл не найден"));
    }

    @Transactional(readOnly = true)
    public List<FileResponseDto> list(FileStatus status) {
        return (status == null ? repo.findAll() : repo.findByStatus(status))
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }
}