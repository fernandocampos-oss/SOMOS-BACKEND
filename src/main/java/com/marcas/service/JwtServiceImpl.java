package com.marcas.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {

    private static final String BEARER = "Bearer ";

    private static final String NOMBRES = "nombres";
    private static final String ID = "id";
    private static final String ID_SEDE = "idSede";
    private static final String ID_ZONA_CONTROL = "idZonaControl";
    private static final String ROL = "idRol";
    private static final int EXPIRES_IN_MILLISECOND = 2 * 3600000; // 2h
    @Value(value = "${auth0.issuer}")
    private String issuer;
    @Value(value = "${auth0.secret-key}")
    private String secretKey;

    @Override
    public String createToken(long id, String nombres, int idSede, Integer idZonaControl, int idRol) {
        return JWT.create()
                .withIssuer(issuer)
                .withIssuedAt(new Date())
                .withNotBefore(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRES_IN_MILLISECOND))
                .withClaim(ID, id)
                .withClaim(ID_SEDE, idSede)
                .withClaim(ID_ZONA_CONTROL, idZonaControl)
                .withClaim(NOMBRES, nombres)
                .withClaim(ROL, idRol)
                .sign(Algorithm.HMAC256(secretKey));
    }

    @Override
    public boolean isBearer(String authorization) {
        return authorization != null &&
                authorization.startsWith(BEARER) &&
                authorization.split("\\.").length == 3;
    }

    @Override
    public String nombres(String authorization) {
        return this.verify(authorization).getClaim(NOMBRES).asString();
    }

    @Override
    public int idSede(String authorization) {
        return this.verify(authorization).getClaim(ID_SEDE).asInt();
    }

    @Override
    public Integer idZonaControl(String authorization) {
        return this.verify(authorization).getClaim(ID_ZONA_CONTROL).asInt();
    }

    @Override
    public int id(String authorization) {
        return this.verify(authorization).getClaim(ID).asInt();
    }


    private DecodedJWT verify(String authorization) {

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        return JWT.require(algorithm)
                .withIssuer(issuer).build()
                .verify(authorization.substring(BEARER.length()));

    }

    @Override
    public int idRol(String authorization) {
        DecodedJWT jwt = this.verify(authorization);
        return jwt.getClaim(ROL).asInt();
    }

}