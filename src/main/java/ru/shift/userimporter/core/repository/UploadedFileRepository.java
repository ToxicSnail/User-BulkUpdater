package ru.shift.userimporter.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.shift.userimporter.core.model.UploadedFile;
import ru.shift.userimporter.core.model.FileStatus;

import java.util.List;
import java.util.Optional;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Integer> {

    /** Проверка дубликата по SHA-1 **/
    Optional<UploadedFile> findByHash(String hash);

    /** Получение файлов по статусу (для фильтрво  `/files/statistic?status=`) **/
    List<UploadedFile> findByStatus(FileStatus status);

    List<UploadedFile> findByStatusAndOwner(FileStatus status, String owner);

    List<UploadedFile> findByOwner(String owner);

    Optional<UploadedFile> findByIdAndOwner(Integer id, String owner);

    long countByOwnerAndStatus(String owner, FileStatus status);
}
