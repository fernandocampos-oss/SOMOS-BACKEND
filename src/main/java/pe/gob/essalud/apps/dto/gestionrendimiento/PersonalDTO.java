package pe.gob.essalud.apps.dto.gestionrendimiento;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface PersonalDTO {
    String getNumeroDocumento();
    String getNombres();
    String getApellidos();
    int getIdVotante();
}
