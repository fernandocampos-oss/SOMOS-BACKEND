package pe.gob.essalud.apps.service;

public interface JwtService {

    String createToken(long id, String nombres, int idRol, String codRed, String codUnidad, int idRolAdicional);
    boolean isBearer(String authorization);
    String nombres(String authorization);
    int id(String authorization);
    int idRol(String authorization);
    int idRolAdicional(String authorization);
    String codRed(String authorization);
    String codUnidad(String authorization);

}
