package pe.gob.essalud.apps.dto.inscripcion.request;

import lombok.Data;

import java.util.List;

@Data
public class InscripcionRequestDto {
    private int idInscripcion;
    private List<Integer> inscritos;
    private String descripcion;
    private String imagenBase64;
}
