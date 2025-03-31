package pe.gob.essalud.apps.dto.onomastico.response;

public interface IOnomasticoResponseDto {
    int getIdusuario();
    String getNombres();
    String getApellidos();
    String getCorreo();
    String getFechaNacimiento();
    boolean getEsActivo();
    String getUnidadDescripcion();
}
