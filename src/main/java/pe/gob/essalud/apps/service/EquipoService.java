package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.gestionrendimiento.PersonalDTO;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Equipo;

import java.util.List;

public interface EquipoService {

    void registrarTrabajador(Equipo equipo);

    List<Equipo> getListTrabajadoresByIdUsuarioJefe();

    int eliminarTrabajador(Number idEquipo);

    List<PersonalDTO> listAllVotante();

//    Usuario findUsuarioByNumeroDocumento(String numeroDocumento);

}
