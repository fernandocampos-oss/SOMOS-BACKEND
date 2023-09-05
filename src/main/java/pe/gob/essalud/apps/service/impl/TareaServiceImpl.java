package pe.gob.essalud.apps.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.time.ZoneId;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import pe.gob.essalud.apps.dto.gestionrendimiento.TareaDTO;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Tarea;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.TareaRepository;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.TareaService;

@Service
@RequiredArgsConstructor
@Slf4j
public class TareaServiceImpl implements TareaService {

    private static final String RUTA_IMAGENES_REQUERIMIENTOS = "/imagenes/requerimientos/";
    private static final String FORMATO_IMAGEN_EQUERIMIENTOS = ".png";

    private final TareaRepository tareaRepository;
    private final AuthService authService;

    @Override
    public List<Tarea> listar() {
//        return tareaRepository.findAll();
        return null;
    }

    @Override
    public Tarea registrar(Tarea obj) {
//        return tareaRepository.save(obj);
        return null;
    }

    @Transactional
    @Override
    public Integer registrarTarea(TareaDTO dto) {
        log.info("idRequerimientoUsuario: {}", dto.getRequerimientoUsuario().getIdRequerimientoUsuario());
        log.info("idPoi: {}", dto.getPoi());
        int result = tareaRepository.actualizarPoi(dto.getPoi().getIdPoi(), dto.getRequerimientoUsuario().getIdRequerimientoUsuario());
        int usuarioCreacion = authService.getIdUserSession();
        if (!dto.getListTarea().isEmpty()) {
            for (Tarea i : dto.getListTarea()) {
                tareaRepository.registrarTarea(i.getNombreTarea(), i.getPlazo(),
                        dto.getRequerimientoUsuario().getIdRequerimientoUsuario(), usuarioCreacion,
                        LocalDateTime.now(ZoneId.of("America/Lima")) );
            }
        }
        return dto.getRequerimientoUsuario().getIdRequerimientoUsuario();
    }

    @Override
    public int actualizarTareaAdministrador(String nombreTarea, String plazo, Number idTarea) {
        int usuarioModificacion = authService.getIdUserSession();
        return tareaRepository.actualizarTareaAdministrador(nombreTarea, plazo, usuarioModificacion, LocalDateTime.now(ZoneId.of("America/Lima")), idTarea);
    }


//    @Override
//    public List<Tarea> listarTareaPorRequermientoPersonal(Number idRequerimientoPersonal) {
//        return tareaRepository.listarTareaPorRequermientoPersonal(idRequerimientoPersonal);
//    }
//
//    @Override
//    public List<Tarea> listarTareaPorPersonal(Number idPersonal) {
//        return tareaRepository.listarTareaPorPersonal(idPersonal);
//    }

}
