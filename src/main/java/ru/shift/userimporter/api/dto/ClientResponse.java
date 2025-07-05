package ru.shift.userimporter.api.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ClientResponse {

    String  phone;
    String  name;
    String  lastName;
    String  middleName;
    String  email;
    String  birthdate;
    String  creationTime;
    String  updateTime;
}
