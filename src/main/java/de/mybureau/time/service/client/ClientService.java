package de.mybureau.time.service.client;

import de.mybureau.time.model.Client;
import de.mybureau.time.repository.ClientRepository;
import de.mybureau.time.service.client.exception.ClientNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;


@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Transactional
    public Client addClient(NewClientRequest newClientRequest) {
        final var newClient = new Client();
        newClient.setName(newClientRequest.getName());
        return clientRepository.save(newClient);
    }

    @Transactional
    public List<Client> list() {
        return clientRepository.findAll();
    }

    @Transactional
    public Client get(long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> ClientNotFoundException.forId(id));
    }
    
}
