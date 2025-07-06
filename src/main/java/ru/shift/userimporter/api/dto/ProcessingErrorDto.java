package ru.shift.userimporter.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record ProcessingErrorDto(
        Integer lineNumber,
        String  errorMessage,

        @JsonIgnore
        String  rawData         //отладочное поле для вывода ошибок в ответах
) {}