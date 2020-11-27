package de.bureau.time.api;

import de.bureau.time.api.dto.ClientDto;
import de.bureau.time.service.client.ClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/clients")
public class ClientApi {

    private final ClientService clientService;

    public ClientApi(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public List<ClientDto> list() {
        return clientService.list().stream()
                .map(ClientDto::from)
                .collect(Collectors.toList());
    }
}
