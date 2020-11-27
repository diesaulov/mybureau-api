package de.bureau.time.api;

import de.bureau.time.api.dto.LoginDto;
import de.bureau.time.api.dto.TokenDto;
import de.bureau.time.security.BadCredentialsException;
import de.bureau.time.security.SecurityService;
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