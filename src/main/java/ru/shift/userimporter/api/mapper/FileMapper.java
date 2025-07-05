package ru.shift.userimporter.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.shift.userimporter.api.dto.*;
import ru.shift.userimporter.core.model.FileMeta;
import ru.shift.userimporter.core.model.ProcessingError;

@Mapper(componentModel = "spring")
public interface FileMapper {
    /* краткая статистика без ошибок */
    default FileStatisticDto toStatDto(FileMeta m) {
        int inserted = m.getValidRows() != null ? m.getValidRows() : 0;
        int errors   = m.getInvalidRows() != null ? m.getInvalidRows() : 0;
        int updated  = (m.getProcessedRows() != null ? m.getProcessedRows() : 0) - inserted;
        return new FileStatisticDto(inserted, updated, errors);
    }

    @Mapping(target = "statistic", expression = "java(toStatDto(meta))")
    @Mapping(target = "errors", source = "errors")
    DetailedFileStatisticDto toDetailedDto(FileMeta meta);

    // поле lineNumber совпадает, но явно задаём, чтобы подавить warning
    @Mapping(target = "lineNumber", source = "lineNumber")
    ProcessingErrorDto toErrorDto(ProcessingError err);

    default FileResponseDto toResponseDto(FileMeta m) {
        int inserted = m.getValidRows() == null ? 0 : m.getValidRows();
        int invalid  = m.getInvalidRows() == null ? 0 : m.getInvalidRows();
        int processed = m.getProcessedRows() == null ? 0 : m.getProcessedRows();
        int updated  = processed - inserted - invalid;

        return FileResponseDto.builder()
                .fileId(m.getId())
                .status(m.getStatus())
                .hashCode(m.getHash().hashCode())
                .statistic(FileResponseDto.Stat.builder()
                        .insertedLinesCount(inserted)
                        .updatedLinesCount(updated)
                        .errorProcessedLinesCount(invalid)
                        .build())
                .build();
    }
}