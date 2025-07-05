package ru.shift.userimporter.api.dto;

public record ProcessingErrorDto(
        Integer lineNumber,
        String  errorMessage,
        String  rawData         // скрыть позже в prod
) {}