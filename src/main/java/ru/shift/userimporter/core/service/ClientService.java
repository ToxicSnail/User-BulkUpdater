package ru.shift.userimporter.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shift.userimporter.api.dto.ClientResponse;
import ru.shift.userimporter.api.mapper.ClientMapper;
import ru.shift.userimporter.core.repository.ClientRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository repo;
    private final ClientMapper mapper;

    @Transactional(readOnly = true)
    public List<ClientResponse> getClients(String phone,
                                     String name,
                                     String lastName,
                                     String email) {

        return mapper.toDto(repo.search(phone, name, lastName, email));
    }
}
