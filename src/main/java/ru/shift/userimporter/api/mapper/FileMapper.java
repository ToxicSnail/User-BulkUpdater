package ru.shift.userimporter.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.shift.userimporter.api.dto.DetailedFileStatisticDto;
import ru.shift.userimporter.api.dto.FileResponseDto;
import ru.shift.userimporter.api.dto.FileStatisticDto;
import ru.shift.userimporter.api.dto.ProcessingErrorDto;
import ru.shift.userimporter.core.model.ProcessingError;
import ru.shift.userimporter.core.model.UploadedFile;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface FileMapper {
    /* краткая статистика без ошибок */
    default FileStatisticDto toStatDto(UploadedFile f) {
        int inserted = f.getInsertedRows();
        int updated  = f.getUpdatedRows();
        int errors   = f.getErrors() == null ? 0 : f.getErrors().size(); // invalid

        return new FileStatisticDto(inserted, updated, errors);
    }

    @Mapping(target = "statistic", expression = "java(toStatDto(file))")
    default FileResponseDto toResponseDto(UploadedFile file) {
        return FileResponseDto.builder()
                .fileId(file.getId())
                .status(file.getStatus())          // статус берём как есть
                .hashCode(file.getHash().hashCode())
                .statistic(toStatDto(file))
                .build();
    }

    default ProcessingErrorDto toErrorDto(ProcessingError e) {
        return new ProcessingErrorDto(
                e.getRowNumber(),
                e.getErrorMessage(),
                e.getRawData()
        );
    }

    @Mapping(target = "statistic", expression = "java(toStatDto(file))")
    @Mapping(target = "errors",
            expression = "java(mapErrors(file.getErrors()))")
    DetailedFileStatisticDto toDetailedDto(UploadedFile file);

    default List<ProcessingErrorDto> mapErrors(List<ProcessingError> src) {
        if (src == null || src.isEmpty()) {
            return Collections.emptyList();
        }
        return src.stream()
                .map(this::toErrorDto)
                .collect(Collectors.toList());
    }
}