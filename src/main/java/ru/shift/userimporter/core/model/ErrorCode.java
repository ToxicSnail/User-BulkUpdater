package ru.shift.userimporter.core.model;
import lombok.Getter;

@Getter
public enum ErrorCode {
    EMPTY_LINE("Пустая строка"),
    INVALID_FORMAT("Неверный формат строки (ожидается 6 полей)"),
    INVALID_NAME("Некорректное имя"),
    INVALID_LAST_NAME("Некорректная фамилия"),
    INVALID_MIDDLE_NAME("Некорректное отчество"),
    INVALID_EMAIL("Некорректный e-mail"),
    INVALID_PHONE("Некорректный телефон"),
    INVALID_BIRTHDATE("Некорректная дата рождения/младше 18 лет");

    private final String description;
    ErrorCode(String description) { this.description = description; }
}