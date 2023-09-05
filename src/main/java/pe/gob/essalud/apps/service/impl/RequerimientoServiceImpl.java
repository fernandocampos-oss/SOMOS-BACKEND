package pe.gob.essalud.apps.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.dto.auth.UserSessionDto;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Requerimiento;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.RequerimientoRepository;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.RequerimientoUsuarioRepository;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.RequerimientoService;

@Service
@RequiredArgsConstructor
public class RequerimientoServiceImpl implements RequerimientoService {

    private final RequerimientoRepository requerimientoRepository;
    private final RequerimientoUsuarioRepository requerimientoUsuarioRepository;
    private final AuthService authService;

    @Override
    public List<Requerimiento> listar() {
//        return requerimientoRepository.findAll();
        return null;
    }

    @Override
    public Requerimiento registrar(Requerimiento model) {
        if (model != null) {
            model.setEstado(true);
            model.setFechaCreacion(LocalDateTime.now(ZoneId.of("America/Lima")));
        }
        Requerimiento req = requerimientoRepository.save(model);
        if(req != null) {
            int idUsuario = authService.getIdUserSession();
            String codRed =  authService.getCodRedSession();
//            UserSessionDto usuario= authService.getUserSession();
            String codUnidadOrganizacion= authService.getCodUnidadSession();
            requerimientoUsuarioRepository.registrarRequerimientoUsuario(req.getIdRequerimiento(), idUsuario, 1, codRed, codUnidadOrganizacion, LocalDateTime.now(ZoneId.of("America/Lima"))) ;
        }
        return req;
    }


}

