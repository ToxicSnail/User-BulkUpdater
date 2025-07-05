package ru.shift.userimporter.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.shift.userimporter.core.model.Client;
import ru.shift.userimporter.core.repository.ClientRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository repo;

    @Transactional(readOnly = true)
    public List<Client> findClients(String phone,
                                    String name,
                                    String lastName,
                                    String email) {
        return repo.search(phone, name, lastName, email);
    }
}
