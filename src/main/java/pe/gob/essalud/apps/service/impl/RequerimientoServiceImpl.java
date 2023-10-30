package pe.gob.essalud.apps.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.essalud.apps.dto.publicacion.request.PublicacionRequestDto;
import pe.gob.essalud.apps.exceptions.ValidationException;
import pe.gob.essalud.apps.model.miessalud.Publicacion;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Requerimiento;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.RequerimientoRepository;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.RequerimientoUsuarioRepository;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.RequerimientoService;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequerimientoServiceImpl implements RequerimientoService {

    private final RequerimientoRepository requerimientoRepository;
    private final RequerimientoUsuarioRepository requerimientoUsuarioRepository;
    private final AuthService authService;

    @Override
    public List<Requerimiento> listar() {
        return null;
    }

    @Override
    public Requerimiento registrar(Requerimiento model) {
        if (model != null) {
            model.setEstado(true);
            model.setUsuarioCreacion(authService.getIdUserSession());
            model.setFechaCreacion(LocalDateTime.now(ZoneId.of("America/Lima")));
        }
        Requerimiento req = requerimientoRepository.save(model);
        if(req != null) {
            int idUsuario = authService.getIdUserSession();
            String codRed =  authService.getCodRedSession();
            String codUnidadOrganizacion= authService.getCodUnidadSession();

            LocalDate fecha_actual = LocalDate.now();
            int anio_registro = fecha_actual.getYear();
            requerimientoUsuarioRepository.registrarRequerimientoUsuario(req.getIdRequerimiento(), idUsuario, 1, codRed, codUnidadOrganizacion, LocalDateTime.now(ZoneId.of("America/Lima")), req.isEsJefe(), anio_registro) ;
        }
        return req;
    }

    @Override
    public void modificarRequerimiento(Integer idRequerimiento, Requerimiento request) {
        requerimientoRepository.modificarRequerimiento(request.getNombre(),
                request.getDescripcion(),
                request.getTipoIngreso().getIdTipoIngreso(),
//                request.getIdentificador(),
                LocalDateTime.now(ZoneId.of("America/Lima")),
                authService.getIdUserSession(),
                idRequerimiento);
    }

}

