package ru.shift.userimporter.core.exception;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

/**
 * Валидация одной строки CSV‑файла.
 * Формат строки: lastName,firstName,middleName,email,phone,birthDate
 */
public final class LineValidator {

    private static final Pattern NAME_RE = Pattern.compile("^[А-ЯЁ][а-яёА-ЯЁ'\\- ]{2,49}$");
    private static final Pattern EMAIL_RE = Pattern.compile("^[A-Za-z0-9._%-]+@(shift\\.com|shift\\.ru)$");
    private static final Pattern PHONE_RE = Pattern.compile("^7\\d{10}$");

    public enum Err {
        INVALID_FORMAT,
        INVALID_LAST_NAME,
        INVALID_NAME,
        INVALID_MIDDLE_NAME,
        INVALID_EMAIL,
        INVALID_PHONE,
        INVALID_BIRTHDATE
    }

    /**
     * @return null если строка валидна, иначе Err-код
     */
    public static Err validate(String[] f) {
        if (f.length != 6) return Err.INVALID_FORMAT;
        if (!NAME_RE.matcher(f[0]).matches()) return Err.INVALID_LAST_NAME;
        if (!NAME_RE.matcher(f[1]).matches()) return Err.INVALID_NAME;
        if (!f[2].isBlank() && !NAME_RE.matcher(f[2]).matches()) return Err.INVALID_MIDDLE_NAME;
        if (!EMAIL_RE.matcher(f[3]).matches()) return Err.INVALID_EMAIL;
        if (!PHONE_RE.matcher(f[4]).matches()) return Err.INVALID_PHONE;
        try {
            LocalDate bd = LocalDate.parse(f[5]);
            if (Period.between(bd, LocalDate.now()).getYears() < 18) return Err.INVALID_BIRTHDATE;
        } catch (Exception e) {
            return Err.INVALID_BIRTHDATE;
        }
        return null;
    }

    private LineValidator() {}
}