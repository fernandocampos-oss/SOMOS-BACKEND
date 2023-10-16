package pe.gob.essalud.apps.dto.inscripcion.response;

import lombok.Data;

import java.util.List;

@Data
public class ReporteInscritosDto {

    private int idInscripcion;
    private String descripcion;
    private boolean votacion;
    private boolean votoActivo;
    private List<UsuariosInscritosResponseDto> inscritos;
}
