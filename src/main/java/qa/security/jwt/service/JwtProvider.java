package qa.security.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.security.jwt.auxiliary.RsaKeysInitializer;
import qa.security.jwt.entity.*;
import qa.source.JWTPropertyDataSource;

import javax.servlet.http.HttpServletRequest;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtProvider {

    private final JwtUserDetailsService jwtUserDetailsService;

    private final Algorithm algorithm;
    private final JWTPropertyDataSource propertiesDataSource;

    @Autowired
    public JwtProvider(JwtUserDetailsService jwtUserDetailsService,
                       RsaKeysInitializer rsaKeysInitializer,
                       JWTPropertyDataSource propertiesDataSource) {
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.propertiesDataSource = propertiesDataSource;
        RSAPublicKey publicKey = rsaKeysInitializer.getPublicKey();
        RSAPrivateKey privateKey = rsaKeysInitializer.getPrivateKey();
        algorithm = Algorithm.RSA256(publicKey, privateKey);
    }

    @Nullable
    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer_")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public JwtData createAccess(String email) {
        long expiration = new Date(getCurrentTimeAtMillis() + propertiesDataSource.getJWT_ACCESS_EXPIRATION()).getTime();
        String cleanToken = JWT.create().
                withIssuer("qa")
                .withClaim("type", "access")
                .withSubject(email)
                .withClaim("expm", String.valueOf(expiration))
                //expiration at millis,
                // I need millisecond precision an ordinary long for some reason does not give me this
                .sign(algorithm);
        return new JwtData(cleanToken, expiration);
    }

    public JwtData createRefresh(String email) {
        long expiration = new Date(getCurrentTimeAtMillis() + propertiesDataSource.getJWT_REFRESH_EXPIRATION()).getTime();
        String cleanToken = JWT.create().
                withIssuer("qa")
                .withClaim("type", "refresh")
                .withSubject(email)
                .withClaim("expm", String.valueOf(expiration))
                .sign(algorithm);
        return new JwtData(cleanToken, expiration);
    }

    public JwtIntermediateDateTransport validate(String cleanToken) {
        try {
            DecodedJWT jwtDecoded = JWT.require(algorithm)
                    .withIssuer("qa")
                    .build()
                    .verify(cleanToken);
            long expAtMillis = Long.parseLong(jwtDecoded.getClaim("expm").asString());
            JwtType type = jwtDecoded.getClaim("type").asString().equals("access") ? JwtType.ACCESS : JwtType.REFRESH;

            JwtAuthenticationData data = (JwtAuthenticationData) jwtUserDetailsService.loadUserByUsername(jwtDecoded.getSubject());
            if (data == null)
                return new JwtIntermediateDateTransport(JwtStatus.INVALID);

            JwtStatus status = validateExpiration(expAtMillis, data, type);
            return new JwtIntermediateDateTransport(status, type, new JwtClaims(jwtDecoded.getClaims()), data);
        } catch (JWTVerificationException e) {
            return new JwtIntermediateDateTransport(JwtStatus.INVALID);
        }
    }

    private JwtStatus validateExpiration(Long tokenExpirationAtMills,
                                                            JwtAuthenticationData data,
                                                            JwtType type) {
        if (getCurrentTimeAtMillis() > tokenExpirationAtMills
                || type == JwtType.ACCESS && isTokenNotActual(tokenExpirationAtMills, data.getAccessTokenExpirationDateAtMills())
                || type == JwtType.REFRESH && isTokenNotActual(tokenExpirationAtMills, data.getRefreshTokenExpirationDateAtMillis()))
            return JwtStatus.EXPIRED;
        return JwtStatus.VALID;
    }

    private long getCurrentTimeAtMillis() {
        return LocalDateTime.now()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }

    private boolean isTokenNotActual(long tokenExpiration, long actualTokenExpiration) {
        return tokenExpiration != actualTokenExpiration;
    }
}