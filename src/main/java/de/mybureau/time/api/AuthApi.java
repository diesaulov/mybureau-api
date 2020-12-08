package de.mybureau.time.api;

import de.mybureau.time.api.dto.LoginDto;
import de.mybureau.time.api.dto.TokenDto;
import de.mybureau.time.security.BadCredentialsException;
import de.mybureau.time.security.SecurityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
class AuthApi {

    private final SecurityService securityService;

    AuthApi(SecurityService securityService) {
        this.securityService = securityService;
    }

    @PostMapping("/login")
    ResponseEntity<TokenDto> login(@RequestBody LoginDto loginDto) {
        try {
            return ResponseEntity.ok(new TokenDto(securityService.login(loginDto.email, loginDto.password)));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}