package de.mybureau.time.service.client.exception;

import de.mybureau.time.service.DomainException;

public class ClientNotFoundException extends DomainException {

    private ClientNotFoundException(String message) {
        super(ClientErrors.CLIENT_NOT_FOUND, message);
    }

    public static ClientNotFoundException forId(long id) {
        return new ClientNotFoundException(String.format("Client with id '%s' cannot be found!", id));
    }

    public static ClientNotFoundException forCode(String clientCode) {
        return new ClientNotFoundException(String.format("Client with code '%s' cannot be found!", clientCode));
    }
}
