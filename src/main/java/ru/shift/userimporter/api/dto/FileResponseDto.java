package ru.shift.userimporter.api.dto;

import lombok.Builder;
import lombok.Value;
import ru.shift.userimporter.core.model.FileStatus;

@Value
@Builder
public class FileResponseDto {

    int     fileId;
    FileStatus  status;

    FileStatisticDto statistic;
    int     hashCode;
}
