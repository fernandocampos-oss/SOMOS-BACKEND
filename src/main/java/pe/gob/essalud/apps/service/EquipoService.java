package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.gestionrendimiento.request.CargaMasivaVotanteDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.UpdateEvidenciaDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.UpdateVotanteDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.TrabajadorResponseDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.VotantePlanillaResponseDto;
import pe.gob.essalud.apps.model.miessalud.Votante;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Equipo;

import java.util.List;

public interface EquipoService {

    void registrarTrabajador(Equipo equipo);
    void registrarEvaluador(Equipo equipo);
    List<Equipo> getListTrabajadoresByIdUsuarioJefe();
    String getListEvaluadorByIdUsuarioJefe();
    int eliminarTrabajador(Number idEquipo);
    List<TrabajadorResponseDto> listAllVotante();
    Votante getVotanteByIdUsuario();
    List<Votante> findVotanteByNombre(String nombre);
    List<VotantePlanillaResponseDto> findVotanteByNombre2(String nombre);
    List<Votante> findAllVotantePerfil();
    void modificarPerfilVotante(int id, UpdateVotanteDto request);
    List<CargaMasivaVotanteDto> cargaMasivaVotante(List<CargaMasivaVotanteDto> listVotantes);

}
