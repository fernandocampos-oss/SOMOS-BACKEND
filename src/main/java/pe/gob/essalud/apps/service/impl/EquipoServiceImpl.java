package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.base.BaseService;
import pe.gob.essalud.apps.dto.gestionrendimiento.PersonalDTO;
import pe.gob.essalud.apps.exceptions.ValidationException;
import pe.gob.essalud.apps.model.miessalud.Votante;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Equipo;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.EquipoRepository;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.EquipoService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EquipoServiceImpl implements EquipoService {

    private final EquipoRepository equipoRepository;
    private final AuthService authService;

    @Override
    public void registrarTrabajador(Equipo equipo) {
        log.info(">>>>> [{}]", authService.getIdUserSession());
        if (equipo != null) {
            Votante getJefeVotante = equipoRepository.getVotanteByIdUsuario(authService.getIdUserSession());
            log.info(">>>>>getJefeVotante [{}]", getJefeVotante);
            if(getJefeVotante != null){
                Votante votante = new Votante();
                votante.setIdVotante(getJefeVotante.getIdVotante());
                equipo.setJefe(votante);
                equipo.setEsActivo(true);
                equipo.setUsuarioCreacion(authService.getIdUserSession());
            }else{
                throw new ValidationException("No puede registrar trabajador porque no cuenta con usuario en votantes");
            }
        }
        Equipo model = equipoRepository.save(equipo);
    }

    @Override
    public List<Equipo> getListTrabajadoresByIdUsuarioJefe() {
        int idLider = authService.getIdUserSession();
        return equipoRepository.getListTrabajadoresByIdUsuarioJefe(idLider);
    }

    @Override
    public int eliminarTrabajador(Number idEquipo) {
        return equipoRepository.eliminarTrabajador(idEquipo);
    }

    @Override
    public List<PersonalDTO> listAllVotante() {
        return equipoRepository.listAllVotante();
    }

//    @Override
//    public Usuario findUsuarioByNumeroDocumento(String numeroDocumento) {
//        return liderEquipoRepository.findUsuarioByNumeroDocumento(numeroDocumento);
//    }

}
