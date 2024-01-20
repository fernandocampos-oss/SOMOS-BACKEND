package pe.gob.essalud.apps.dto.inscripcion.response;

import lombok.Data;

import java.util.List;

@Data
public class InscripcionDatosResponseDto {

    private boolean imagenActiva;
    private String imagenDescripcion;
    private boolean textoActivo;
    private String textoDescripcion;
    private boolean grupoActivo;
    private Integer grupoLongitud;
    private List<String> responsables;
}
