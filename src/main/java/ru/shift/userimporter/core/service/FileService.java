package ru.shift.userimporter.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.shift.userimporter.config.StorageProperties;
import ru.shift.userimporter.core.model.FileMeta;
import ru.shift.userimporter.core.model.FileStatus;
import ru.shift.userimporter.core.repository.FileMetaRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final StorageProperties props;
    private final FileMetaRepository repo;

    @Transactional
    public Integer upload(MultipartFile file) throws IOException {
        /* 1. hash + дубликаты */
        String sha1 = DigestUtils.sha1Hex(file.getInputStream());
        repo.findByHash(sha1).ifPresent(f -> {
            throw new IllegalStateException("Файл уже был загружен");
        });

        /* 2. копируем на диск */
        Path dir = props.getLocation();
        Files.createDirectories(dir);
        Path target = dir.resolve(UUID.randomUUID() + "_" + file.getOriginalFilename());
        Files.copy(file.getInputStream(), target);

        /* 3. создаём запись */
        FileMeta meta = new FileMeta();
        meta.setOriginalFilename(file.getOriginalFilename());
        meta.setStoragePath(target.toString());
        meta.setHash(sha1);
        meta.setStatus(FileStatus.NEW);

        return repo.save(meta).getId();
    }
}
