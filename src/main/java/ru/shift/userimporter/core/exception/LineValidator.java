package ru.shift.userimporter.core.exception;

import jakarta.validation.ValidationException;
import lombok.Getter;
import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;
import java.time.format.DateTimeParseException;

/**
 * Валидация одной строки CSV‑файла.
 * Формат строки: lastName,firstName,middleName,email,phone,birthDate
 */
public final class LineValidator {

    private static final Pattern NAME_RE   = Pattern.compile("^[А-ЯЁ][а-яёА-ЯЁ'\\- ]{2,49}$");
    private static final Pattern EMAIL_RE  = Pattern.compile("^[A-Za-z0-9._%-]+@(shift\\.com|shift\\.ru)$");
    private static final Pattern PHONE_RE  = Pattern.compile("^7\\d{10}$");

    @Getter
    public enum Err {
        EMPTY_LINE("Пустая строка"),
        INVALID_FORMAT("Неверный формат строки (ожидается 6 полей)"),
        INVALID_LAST_NAME("Некорректная фамилия"),
        INVALID_NAME("Некорректное имя"),
        INVALID_MIDDLE_NAME("Некорректное отчество"),
        INVALID_EMAIL("Некорректный e-mail"),
        INVALID_PHONE("Некорректный телефон"),
        INVALID_BIRTHDATE("Некорректная дата рождения/младше 18 лет");

        private final String description;
        Err(String description) { this.description = description; }
    }

    public static void validate(String[] f) {
        if (f == null || f.length == 0 || String.join("", f).isBlank()) {
            throw new ValidationException(Err.EMPTY_LINE.name());
        }
        if (f.length != 6)                                 throw new ValidationException(Err.INVALID_FORMAT.name());
        if (!NAME_RE.matcher(f[0]).matches())              throw new ValidationException(Err.INVALID_LAST_NAME.name());
        if (!NAME_RE.matcher(f[1]).matches())              throw new ValidationException(Err.INVALID_NAME.name());
        if (!f[2].isBlank() && !NAME_RE.matcher(f[2]).matches())
            throw new ValidationException(Err.INVALID_MIDDLE_NAME.name());
        if (!EMAIL_RE.matcher(f[3]).matches())             throw new ValidationException(Err.INVALID_EMAIL.name());
        if (!PHONE_RE.matcher(f[4]).matches())             throw new ValidationException(Err.INVALID_PHONE.name());
        try {
            LocalDate bd = LocalDate.parse(f[5]);
            if (Period.between(bd, LocalDate.now()).getYears() < 18) throw new ValidationException(Err.INVALID_BIRTHDATE.name());
        } catch (DateTimeParseException ex) {
            throw new ValidationException(Err.INVALID_BIRTHDATE.name());
        }
    }

    private LineValidator() {}
}
