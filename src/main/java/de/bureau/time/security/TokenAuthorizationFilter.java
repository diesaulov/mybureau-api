package de.bureau.time.security;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class TokenAuthorizationFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(TokenAuthorizationFilter.class);

    private final UserDetailsService userDetailsService;
    private final SecurityService securityService;

    public TokenAuthorizationFilter(UserDetailsService userDetailsService,
                                    SecurityService securityService) {
        this.userDetailsService = userDetailsService;
        this.securityService = securityService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {

        token(request)
                .flatMap(this::validate)
                .flatMap(this::loadUserDetails)
                .map(this::convert)
                .ifPresent(auth -> SecurityContextHolder.getContext().setAuthentication(auth));

        filterChain.doFilter(request, response);
    }

    private Optional<String> token(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(StringUtils::isNotBlank)
                .filter(h -> h.startsWith("Bearer "))
                .map(h -> h.replace("Bearer ", ""))
                .filter(StringUtils::isNotBlank);
    }

    private Optional<String> validate(String token) {
        try {
            return Optional.of(securityService.validateToken(token));
        } catch (InvalidTokenException e) {
            return Optional.empty();
        }
    }

    private Optional<UserDetails> loadUserDetails(String username) {
        try {
            return Optional.of(userDetailsService.loadUserByUsername(username));
        } catch (UsernameNotFoundException e) {
            logger.warn("Authorization failed. No user found for username: {}", username);
            return Optional.empty();
        }
    }

    private Authentication convert(UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
    }
}
