package de.bureau.time.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.google.common.base.Preconditions.checkArgument;
import static de.bureau.time.utils.DateTimeUtils.*;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Component
public class JwtTokenProvider {

    private final String secret;
    private final String issuer;
    private final long ttlInSeconds;

    public JwtTokenProvider(@Value("${security.token.secret}") String secret,
                            @Value("${security.token.issuer}") String issuer,
                            @Value("${security.token.ttlInSeconds}") long ttlInSeconds) {
        checkArgument(isNotEmpty(secret) && secret.getBytes().length >= 32, "The secret should be at least 32 bytes long");
        checkArgument(isNotEmpty(issuer), "Issuer should be provided");
        checkArgument(ttlInSeconds >= 0, "TTL should be >= 0");

        this.secret = secret;
        this.issuer = issuer;
        this.ttlInSeconds = ttlInSeconds;
    }

    public String issue(String username) {
        var expiresAt = nowInUtc().plusSeconds(ttlInSeconds);
        return issueToken(username, expiresAt);
    }

    private String issueToken(String username, LocalDateTime expiresAt) {
        return Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS512)
                .setIssuer(issuer)
                .setSubject(username)
                .setIssuedAt(nowDateInUtc())
                .setExpiration(fromUtcLocalDateTime(expiresAt))
                .compact();
    }

    public String validate(String token) throws InvalidTokenException {
        try {
            var tokenParser = Jwts.parserBuilder()
                    .setSigningKey(secret.getBytes())
                    .build();

            var parsedToken = tokenParser.parse(token);
            var tokenBody = checkIfTokenMatchesItsOrigin(parsedToken);

            return tokenBody.getSubject();
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException e) {
            throw new InvalidTokenException(e);
        }
    }

    private Claims checkIfTokenMatchesItsOrigin(Jwt token) throws InvalidTokenException {
        final var tokenBody = (Claims) token.getBody();

        if(!issuer.equals(tokenBody.getIssuer())) {
            throw new InvalidTokenException("Token issuer mismatch");
        }

        return tokenBody;
    }

}
