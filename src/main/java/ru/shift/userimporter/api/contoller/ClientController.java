package ru.shift.userimporter.api.contoller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.shift.userimporter.api.dto.ClientResponse;
import ru.shift.userimporter.api.mapper.ClientMapper;
import ru.shift.userimporter.core.service.ClientService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
@Validated
@RequiredArgsConstructor
public class ClientController {

    private final ClientService  service;
    private final ClientMapper mapper;

    @GetMapping
    public List<ClientResponse> getClients(
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email)
    {
        return mapper.toDto(service.findClients(phone, name, lastName, email));
    }
}
