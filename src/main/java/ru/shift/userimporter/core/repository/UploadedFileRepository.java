package ru.shift.userimporter.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.shift.userimporter.core.model.UploadedFile;
import ru.shift.userimporter.core.model.FileStatus;

import java.util.List;
import java.util.Optional;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Integer> {

    /** Проверка дубликата по SHA-1 **/
    Optional<UploadedFile> findByHash(String hash);

    /** Получение файлов по статусу (для фильтра `/files/statistics?status=`). **/
    List<UploadedFile> findByStatus(FileStatus status);
}
