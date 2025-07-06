package ru.shift.userimporter.core.service;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;
import ru.shift.userimporter.core.exception.LineValidator;
import ru.shift.userimporter.core.model.Client;

import java.time.LocalDate;

@Component
public class ClientCsvParser {

//    private ClientCsvParser() {}

    public Client parse(String line) {
        String[] f   = line.split(",", -1);
        LineValidator.validate(f);

        Client c = new Client();
        c.setLastName(f[0]);
        c.setFirstName(f[1]);
        c.setMiddleName(f[2].isBlank() ? null : f[2]);
        c.setEmail(f[3]);
        c.setPhone(f[4]);
        c.setBirthDate(LocalDate.parse(f[5]));
        return c;
    }
}
