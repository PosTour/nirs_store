package ru.bmstu.nirs.store.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bmstu.nirs.store.domain.Client;
import ru.bmstu.nirs.store.repository.ClientRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClientService {

    private ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public void save(Client client) {
        clientRepository.save(client);
    }

    @Transactional(readOnly = true)
    public Optional<Client> findByPhone(String phone) {
        return clientRepository.findClientByPhone(phone);
    }

    @Transactional(readOnly = true)
    public Optional<Client> findById(int id) {
        return clientRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public void update(int id, Client updatedClient) {
        var client = clientRepository.findById(id);

        if (client.isPresent()) {
            updatedClient.setId(id);
            clientRepository.save(updatedClient);
        }
    }

    public void delete(int id) {
        clientRepository.deleteById(id);
    }
}
