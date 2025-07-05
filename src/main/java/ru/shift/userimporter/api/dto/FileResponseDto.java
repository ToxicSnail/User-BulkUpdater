package ru.shift.userimporter.api.dto;

import lombok.Builder;
import lombok.Value;
import ru.shift.userimporter.core.model.FileStatus;

@Value
@Builder
public class FileResponseDto {

    Integer     fileId;
    FileStatus  status;

    Stat        statistic;
    Integer     hashCode;

    @Value
    @Builder
    public static class Stat {
        Integer insertedLinesCount;
        Integer updatedLinesCount;
        Integer errorProcessedLinesCount;
    }
}
