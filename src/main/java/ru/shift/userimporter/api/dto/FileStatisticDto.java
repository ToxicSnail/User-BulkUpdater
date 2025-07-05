package ru.shift.userimporter.api.dto;

public record FileStatisticDto(
        int insertedLinesCount,
        int updatedLinesCount,
        int errorProcessedLinesCount
) {}