package de.mybureau.time.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

public class SecurityService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public SecurityService(AuthenticationManager authenticationManager,
                           JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String login(String username, String password) throws BadCredentialsException {
        try {
            var authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            var email = ((String) authentication.getPrincipal());
            return jwtTokenProvider.issue(email);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException();
        }
    }

    public String validateToken(String token) throws InvalidTokenException {
        return jwtTokenProvider.validate(token);
    }
}
