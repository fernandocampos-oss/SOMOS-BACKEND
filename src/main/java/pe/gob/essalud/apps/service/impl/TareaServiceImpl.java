package pe.gob.essalud.apps.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.ZoneId;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import pe.gob.essalud.apps.common.util.UploadUtil;
import pe.gob.essalud.apps.dto.gestionrendimiento.EvidenciaResponseDTO;
import pe.gob.essalud.apps.dto.gestionrendimiento.EvidenciaRequestDTO;
import pe.gob.essalud.apps.dto.gestionrendimiento.TareaDTO;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.*;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.EvidenciaRepository;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.TareaRepository;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.TareaService;

@Service
@RequiredArgsConstructor
@Slf4j
public class TareaServiceImpl implements TareaService {

    private static final String RUTA_IMAGENES_EVIDENCIA = "/imagenes/requerimientos/";
    private static final String FORMATO_IMAGEN_EVIDENCIA = ".png";

    private final TareaRepository tareaRepository;
    private final AuthService authService;
    private final EvidenciaRepository evidenciaRepository;

    @Value("${upload-path}")
    private String uploadPath;

    @Override
    public List<Tarea> listar() {
        return null;
    }

    @Override
    public Tarea registrar(Tarea obj) {
        return null;
    }

    @Transactional
    @Override
    public Integer registrarTarea(TareaDTO dto) {
        int result = tareaRepository.actualizarPoi(dto.getPoi().getIdPoi(), dto.getRequerimientoUsuario().getIdRequerimientoUsuario());
        int usuarioCreacion = authService.getIdUserSession();
        if (!dto.getListTarea().isEmpty()) {
            for (Tarea i : dto.getListTarea()) {
                tareaRepository.registrarTarea(i.getNombreTarea(), i.getPlazo(),
                        dto.getRequerimientoUsuario().getIdRequerimientoUsuario(), usuarioCreacion,
                        LocalDateTime.now(ZoneId.of("America/Lima")));
            }
        }
        return dto.getRequerimientoUsuario().getIdRequerimientoUsuario();
    }

    @Override
    public int actualizarTareaAdministrador(String nombreTarea, String plazo, Number idTarea) {
        int usuarioModificacion = authService.getIdUserSession();
        return tareaRepository.actualizarTareaAdministrador(nombreTarea, plazo, usuarioModificacion, LocalDateTime.now(ZoneId.of("America/Lima")), idTarea);
    }

    @Transactional
    @Override
    public long crearEvidencia(EvidenciaRequestDTO request) {
        Evidencia evidencia = new Evidencia();
        evidencia.setDescripcion(request.getDescripcion());
        evidencia.setFechaCreacion(LocalDateTime.now(ZoneId.of("America/Lima")));
        evidencia.setEstado(true);
        evidencia.setUsuarioCreacion(authService.getIdUserSession());
        Tarea tarea = new Tarea();
        tarea.setIdTarea(request.getTarea().getIdTarea());
        evidencia.setTarea(tarea);
        evidencia.setNombreImagen(request.getNombreImagen());
        evidencia.setSizeImagen(request.getSizeImagen());
        evidencia.setTipoImagen(request.getTipoImagen());
        evidencia.setExtension(request.getExtension());
        evidencia.setPorcentajeAvance(request.getPorcentajeAvance());
        Evidencia result = evidenciaRepository.save(evidencia);

        log.info("IdRequerimiento [{}]", request.getIdRequerimiento());
        Requerimiento requerimiento = tareaRepository.getByIdRequerimiento(request.getIdRequerimiento());
        int nuevoPorcentajeAvance = (requerimiento.getPorcentajeAvance() + request.getPorcentajeAvance());
        log.info("incremento avance [{}]", nuevoPorcentajeAvance);
        tareaRepository.actualizaPorcentajeAvanceRequerimiento(nuevoPorcentajeAvance, request.getIdRequerimiento());

        String rutaImagen = uploadPath + RUTA_IMAGENES_EVIDENCIA + result.getIdEvidencia() + FORMATO_IMAGEN_EVIDENCIA;
        rutaImagen = UploadUtil.saveFileBase64(rutaImagen, request.getImagenBase64());

        tareaRepository.actualizarRutaImagenEvidencia(rutaImagen, result.getIdEvidencia());

        return result.getIdEvidencia();
    }

    @Override
    public List<EvidenciaResponseDTO> listarEvidenciaTarea(Integer idTarea) {
        List<Evidencia> list = tareaRepository.listarEvidenciaTarea(idTarea);
        List<EvidenciaResponseDTO> listDto = new ArrayList<>();
        for (Evidencia item : list) {
            log.info("recorrido -i- [{}]", item.getIdEvidencia());
            String baseImagen = UploadUtil.getFileBase64(item.getRutaImagen());
            EvidenciaResponseDTO dto = new EvidenciaResponseDTO();
            dto.setIdEvidencia(item.getIdEvidencia());
            dto.setDescripcion(item.getDescripcion());
            dto.setFechaCreacion(item.getFechaCreacion());
            dto.setImagenBase64(baseImagen);
            dto.setExtension(item.getExtension());
            dto.setNombreImagen(item.getNombreImagen());
            dto.setPorcentajeAvance(item.getPorcentajeAvance());
            listDto.add(dto);
        }
        return listDto;
    }

    @Override
    public List<Poi> listarAllPoi() {
        return tareaRepository.listarAllPoi();
    }

    @Override
    public List<TipoIngreso> listarAllTipoIngreso() {
        return tareaRepository.listarAllTipoIngreso();
    }

}
