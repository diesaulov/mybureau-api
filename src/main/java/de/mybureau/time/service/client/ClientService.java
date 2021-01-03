package de.mybureau.time.service.client;

import de.mybureau.time.model.Client;
import de.mybureau.time.repository.ClientRepository;
import de.mybureau.time.service.client.exception.ClientNotFoundException;
import de.mybureau.time.utils.DateTimeHelper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;


@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final DateTimeHelper dateTimeHelper;

    public ClientService(ClientRepository clientRepository, DateTimeHelper dateTimeHelper) {
        this.clientRepository = clientRepository;
        this.dateTimeHelper = dateTimeHelper;
    }

    @Transactional
    public Client addClient(NewClientRequest newClientRequest) {
        final var newClient = new Client();
        newClient.setName(newClientRequest.getName());
        newClient.setCreatedTs(dateTimeHelper.nowInUtc());
        return clientRepository.save(newClient);
    }

    @Transactional
    public List<Client> list() {
        return clientRepository.findAll();
    }

    @Transactional
    public Client findClient(long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> ClientNotFoundException.forId(id));
    }
    
}
