package de.mybureau.time.api.dto;

import de.mybureau.time.model.Client;

public class ClientDto {
    public String id;
    public String code;
    public String name;

    public static ClientDto from(Client client) {
        final var dto = new ClientDto();
        dto.id = Long.toString(client.getId());
        dto.name = client.getName();
        return dto;
    }
}
