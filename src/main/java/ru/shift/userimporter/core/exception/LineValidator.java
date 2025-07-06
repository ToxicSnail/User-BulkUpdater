package ru.shift.userimporter.core.exception;

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

    /**
     * @return null если строка валидна, иначе Err-код
     */
    public static Err validate(String[] f) {
        if (f == null || f.length == 0 || String.join("", f).isBlank()) {
            return Err.EMPTY_LINE;
        }
        if (f.length != 6)                                 return Err.INVALID_FORMAT;
        if (!NAME_RE.matcher(f[0]).matches())              return Err.INVALID_LAST_NAME;
        if (!NAME_RE.matcher(f[1]).matches())              return Err.INVALID_NAME;
        if (!f[2].isBlank() && !NAME_RE.matcher(f[2]).matches())
            return Err.INVALID_MIDDLE_NAME;
        if (!EMAIL_RE.matcher(f[3]).matches())             return Err.INVALID_EMAIL;
        if (!PHONE_RE.matcher(f[4]).matches())             return Err.INVALID_PHONE;
        try {
            LocalDate bd = LocalDate.parse(f[5]);
            if (Period.between(bd, LocalDate.now()).getYears() < 18) return Err.INVALID_BIRTHDATE;
        } catch (DateTimeParseException ex) {
            return Err.INVALID_BIRTHDATE;
        }
        return null;
    }

    private LineValidator() {}
}
