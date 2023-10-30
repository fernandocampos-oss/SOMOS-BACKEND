package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.base.BaseService;
import pe.gob.essalud.apps.dto.gestionrendimiento.PersonalDTO;
import pe.gob.essalud.apps.model.miessalud.Usuario;
import pe.gob.essalud.apps.model.miessalud.Votante;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.LiderEquipo;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.RequerimientoUsuario;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.LiderEquipoRepository;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.LiderEquipoService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LiderEquipoServiceImpl extends BaseService implements LiderEquipoService {

    private final LiderEquipoRepository liderEquipoRepository;
    private final AuthService authService;

    @Override
    public Integer save(LiderEquipo liderEquipo) {
        if (liderEquipo != null) {
            Usuario usuarioLider = new Usuario();
            usuarioLider.setIdUsuario(authService.getIdUserSession());

            liderEquipo.setLider(usuarioLider);
            liderEquipo.setEsActivo(true);
            liderEquipo.setUsuarioModificacion(authService.getIdUserSession());
            liderEquipo.setFechaCreacion(LocalDateTime.now(ZoneId.of("America/Lima")));
        }
        LiderEquipo model = new LiderEquipo();
        model = liderEquipoRepository.save(liderEquipo);
        return model.getIdLiderEquipo();
    }

    @Override
    public List<LiderEquipo> listarIntegrantesPorLider() {
        long idLider = authService.getIdUserSession();
        return liderEquipoRepository.listarIntegrantesPorLider(idLider);
    }

    @Override
    public int eliminarIntegrante(Number idIntegrante) {
        return liderEquipoRepository.eliminarIntegrante(idIntegrante);
    }

//    @Override
//    public List<Votante> listAllVotante() {
//        return liderEquipoRepository.listAllVotante();
//    }

//    @Override
//    public Usuario findUsuarioByNumeroDocumento(String numeroDocumento) {
//        return liderEquipoRepository.findUsuarioByNumeroDocumento(numeroDocumento);
//    }

}
