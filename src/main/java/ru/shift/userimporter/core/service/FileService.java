package ru.shift.userimporter.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.shift.userimporter.config.StorageProperties;
import ru.shift.userimporter.core.exception.ConflictException;
import ru.shift.userimporter.core.model.UploadedFile;
import ru.shift.userimporter.core.model.FileStatus;
import ru.shift.userimporter.core.repository.UploadedFileRepository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final StorageProperties props;
    private final UploadedFileRepository repo;

    @Transactional
    public int upload(MultipartFile file) {
        try {
            /* 1. копируем на диск */
            Path dir = props.getLocation();
            Files.createDirectories(dir);

            Path target = dir.resolve(
                    UUID.randomUUID() + "_" + file.getOriginalFilename()
            );
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target);
            }

            /* 2. считаем SHA-1 */
            String sha1;
            try (InputStream in = Files.newInputStream(target)) {
                sha1 = DigestUtils.sha1Hex(in);
            }

            /* 3. проверяем дубликаты */
            repo.findByHash(sha1).ifPresent(f -> {
                throw new ConflictException("Файл уже был загружен");
            });

            /* 4. создаём запись */
            UploadedFile meta = new UploadedFile();
            meta.setOriginalFilename(file.getOriginalFilename());
            meta.setStoragePath(target.toString());
            meta.setHash(sha1);
            meta.setStatus(FileStatus.NEW);

            return repo.save(meta).getId();
        } catch (IOException ex) {
            throw new RuntimeException("Не удалось сохранить файл", ex);
        }
    }
}
