package com.pl.przytula.dietapp.security.oauth2;

import com.pl.przytula.dietapp.config.AppAuthProperties;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipal;
import java.util.Date;

@Service
@Slf4j
public class TokenJWTProvider {

    private AppAuthProperties appAuthProperties;


    public TokenJWTProvider(AppAuthProperties appAuthProperties) {
        this.appAuthProperties = appAuthProperties;
    }

    public String createToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + appAuthProperties.getAuth().getTokenExpirationMsec());
        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, appAuthProperties.getAuth().getTokenSecret())
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(appAuthProperties.getAuth().getTokenSecret())
                .parseClaimsJws(token)
                .getBody();
        return  Long.parseLong(claims.getId());
    }

    public boolean validateToken(String token) {
        try{
            Jwts.parser().setSigningKey(appAuthProperties.getAuth().getTokenSecret());
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid token signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid token");
        } catch (ExpiredJwtException ex) {
            log.error("Expire token signature");
        }
    }
}
