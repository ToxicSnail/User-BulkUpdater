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
import ru.shift.userimporter.core.service.security.SecurityUtils;
import ru.shift.userimporter.core.service.AccessPolicyService;

import java.util.List;
@Service
@RequiredArgsConstructor
public class StatisticService {
    private final UploadedFileRepository repo;
    private final FileMapper         mapper;
    private final AccessPolicyService policy;

    @Transactional(readOnly = true)
    public DetailedFileStatisticDto detailed(Integer fileId) {
        SecurityUtils.CurrentUser user = SecurityUtils.currentUser();
        UploadedFile file = loadFileForUser(fileId, user);
        policy.assertFileRead(user, file);
        return mapper.toDetailedDto(file);
    }

    @Transactional(readOnly = true)
    public List<FileResponseDto> list(FileStatus status) {
        SecurityUtils.CurrentUser user = SecurityUtils.currentUser();
        List<UploadedFile> files;
        if (user.isAdmin() || user.isAuditor()) {
            files = status == null ? repo.findAll() : repo.findByStatus(status);
        } else if (user.isOperator()) {
            files = status == null
                    ? repo.findByOwner(user.username())
                    : repo.findByStatusAndOwner(status, user.username());
        } else {
            throw new EntityNotFoundException("File not found");
        }
        return files
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    private UploadedFile loadFileForUser(Integer fileId, SecurityUtils.CurrentUser user) {
        if (user.isAdmin() || user.isAuditor()) {
            return repo.findById(fileId)
                    .orElseThrow(() -> new EntityNotFoundException("�������> �?�� �?�����?��?"));
        }
        if (user.isOperator()) {
            return repo.findByIdAndOwner(fileId, user.username())
                    .orElseThrow(() -> new EntityNotFoundException("�������> �?�� �?�����?��?"));
        }
        throw new EntityNotFoundException("�������> �?�� �?�����?��?");
    }
}
