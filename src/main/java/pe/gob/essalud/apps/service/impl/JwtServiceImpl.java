package pe.gob.essalud.apps.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import pe.gob.essalud.apps.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {

    private static final String BEARER = "Bearer ";

    private static final String NOMBRES = "nombres";
    private static final String ID = "id";
    private static final String ROL = "idRol";
    private static final String COD_RED = "codRed";
    private static final String COD_UNIDAD = "codUnidad";
    private static final int EXPIRES_IN_MILLISECOND = 2 * 3600000; // 2h
    @Value(value = "${auth0.issuer}")
    private String issuer;
    @Value(value = "${auth0.secret-key}")
    private String secretKey;

    @Override
    public String createToken(long id, String nombres, int idRol, String codRed, String codUnidad) {
        return JWT.create()
                .withIssuer(issuer)
                .withIssuedAt(new Date())
                .withNotBefore(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRES_IN_MILLISECOND))
                .withClaim(ID, id)
                .withClaim(NOMBRES, nombres)
                .withClaim(ROL, idRol)
                .withClaim(COD_RED, codRed)
                .withClaim(COD_UNIDAD, codUnidad)
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

    @Override
    public String codRed(String authorization) {
        return this.verify(authorization).getClaim(COD_RED).asString();
    }

    @Override
    public String codUnidad(String authorization) {
        return this.verify(authorization).getClaim(COD_UNIDAD).asString();
    }

}