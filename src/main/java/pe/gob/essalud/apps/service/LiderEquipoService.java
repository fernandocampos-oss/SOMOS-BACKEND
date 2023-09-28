package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.LiderEquipo;

import java.util.List;

public interface LiderEquipoService {

    Integer save(LiderEquipo liderEquipo);

    List<LiderEquipo> listarIntegrantesPorLider();

    int eliminarIntegrante(Number idIntegrante);

}
