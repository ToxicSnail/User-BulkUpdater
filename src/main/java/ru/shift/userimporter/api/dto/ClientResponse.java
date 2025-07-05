package ru.shift.userimporter.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;

@Value
@Builder
public class ClientResponse {

    String  phone;
    String  name;
    String  lastName;
    String  middleName;
    String  email;
    String  birthdate;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    OffsetDateTime creationTime;

    OffsetDateTime  updateTime;
}
