package pe.gob.essalud.apps.dto.proyecto.request;

import lombok.Data;

import java.util.List;

@Data
public class ProyectoImplementacionRequest {

    private int idProyectoImplementacion;
    private String resultado;
    private String sostenible;
    private String sostenibleFundamento;
    private String replicable;
    private String replicableFundamento;
    private String tecnologia;
    private String tecnologiaFundamento;
    private String beneficio;
    private String archivoBase64;
    private List<String> enfoques;

}
