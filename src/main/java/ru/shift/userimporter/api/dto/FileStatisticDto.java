package ru.shift.userimporter.api.dto;

public record FileStatisticDto(
        Integer insertedLinesCount,
        Integer updatedLinesCount,
        Integer errorProcessedLinesCount
) {}