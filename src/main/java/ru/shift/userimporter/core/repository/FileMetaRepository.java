package ru.shift.userimporter.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.shift.userimporter.core.model.FileMeta;
import ru.shift.userimporter.core.model.FileStatus;

import java.util.List;
import java.util.Optional;

public interface FileMetaRepository extends JpaRepository<FileMeta, Integer> {

    /** Проверка дубликата по SHA-1 **/
    Optional<FileMeta> findByHash(String hash);

    /** Получение файлов по статусу (для фильтра `/files/statistics?status=`). **/
    List<FileMeta> findByStatus(FileStatus status);
}
