package ru.shift.userimporter.api.dto;

import java.util.List;

public record DetailedFileStatisticDto(
        FileStatisticDto statistic,
        List<ProcessingErrorDto> errors
) {}