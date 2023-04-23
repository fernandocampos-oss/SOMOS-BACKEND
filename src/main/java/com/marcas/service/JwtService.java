package com.marcas.service;

public interface JwtService {

    String createToken(long id, String nombres, int idSede, Integer idZonaControl, int idRol);
    boolean isBearer(String authorization);
    String nombres(String authorization);
    int id(String authorization);
    int idSede(String authorization);
    Integer idZonaControl(String authorization);
    int idRol(String authorization);

}
