package ru.shift.userimporter.api.mapper;

import org.mapstruct.*;
import ru.shift.userimporter.api.dto.ClientResponse;
import ru.shift.userimporter.core.model.Client;
import java.util.List;
import java.time.OffsetDateTime;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "name",         source = "firstName")
    @Mapping(target = "birthdate",    source = "birthDate")
    @Mapping(target = "creationTime", source = "createdAt")
    @Mapping(target = "updateTime",   source = "updatedAt")
    ClientResponse toDto(Client client);

    List<ClientResponse> toDto(List<Client> clients);
}