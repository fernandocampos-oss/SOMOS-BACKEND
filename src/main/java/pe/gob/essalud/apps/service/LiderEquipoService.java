package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.gestionrendimiento.PersonalDTO;
import pe.gob.essalud.apps.model.miessalud.Usuario;
import pe.gob.essalud.apps.model.miessalud.Votante;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.LiderEquipo;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.RequerimientoUsuario;

import java.util.List;

public interface LiderEquipoService {

    Integer save(LiderEquipo liderEquipo);

    List<LiderEquipo> listarIntegrantesPorLider();

    int eliminarIntegrante(Number idIntegrante);

//    List<Votante> listAllVotante();

//    Usuario findUsuarioByNumeroDocumento(String numeroDocumento);

}
