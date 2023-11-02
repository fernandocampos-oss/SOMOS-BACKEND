package pe.gob.essalud.apps.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.time.ZoneId;
import java.util.Optional;

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
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.TipoIngresoRepository;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.TareaService;

@Service
@RequiredArgsConstructor
@Slf4j
public class TareaServiceImpl implements TareaService {

    private static final String RUTA_IMAGENES_EVIDENCIA = "/imagenes/requerimientos/";
    private static final String RUTA_PDF_EVIDENCIA = "/pdf/requerimientos/";
//    private static final String FORMATO_IMAGEN_EVIDENCIA = ".png";
    private static final String FORMATO_PDF_EVIDENCIA = ".pdf";

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
        int result = tareaRepository.actualizarPoi(dto.getPoi().getIdActividad(), dto.getRequerimientoUsuario().getIdIndicadorUsuario());
        int usuarioCreacion = authService.getIdUserSession();
        if (!dto.getListTarea().isEmpty()) {
            for (Tarea i : dto.getListTarea()) {
                tareaRepository.registrarTarea(i.getNombre(), i.getPlazo(),
                        dto.getRequerimientoUsuario().getIdIndicadorUsuario(), usuarioCreacion,
                        LocalDateTime.now(ZoneId.of("America/Lima")), i.getPeso(), 0, 0);
            }
        }
        return dto.getRequerimientoUsuario().getIdIndicadorUsuario();
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
        evidencia.setExtension(request.getExtension());
        evidencia.setPorcentajeAvance(request.getPorcentajeAvance());
        Evidencia result = evidenciaRepository.save(evidencia);

        Indicador requerimiento = tareaRepository.getByIdRequerimiento(request.getIdRequerimiento());
//        int nuevoPorcentajeAvance = (requerimiento.getPorcentajeAvance() + request.getPorcentajeAvance());
//        if(nuevoPorcentajeAvance > 100){
//            throw new ValidationException("El porcentaje ingresado excede el 100%");
//        }
//        tareaRepository.actualizaPorcentajeAvanceRequerimiento(nuevoPorcentajeAvance, request.getIdRequerimiento());

        if(result.getExtension().equals("pdf")){
            String rutaArchivo = uploadPath + RUTA_PDF_EVIDENCIA + result.getIdEvidencia() + FORMATO_PDF_EVIDENCIA;
            rutaArchivo = UploadUtil.saveFileBase64(rutaArchivo, request.getImagenBase64());
            tareaRepository.actualizarRutaImagenEvidencia(rutaArchivo, result.getIdEvidencia());

            tareaRepository.actualizarEstadoArchivoTarea(0,1, result.getTarea().getIdTarea());
        }else{
            String rutaImagen = uploadPath + RUTA_IMAGENES_EVIDENCIA + result.getIdEvidencia() + "." + result.getExtension();
            rutaImagen = UploadUtil.saveFileBase64(rutaImagen, request.getImagenBase64());
            tareaRepository.actualizarRutaImagenEvidencia(rutaImagen, result.getIdEvidencia());

            tareaRepository.actualizarEstadoArchivoTarea(1,0, result.getTarea().getIdTarea());
        }

        return result.getIdEvidencia();
    }

    @Override
    public EvidenciaResponseDTO getEvidenciaPorTarea(Integer idTarea) {
        Optional<Evidencia> evidencia = tareaRepository.getEvidenciaPorTarea(idTarea);

        EvidenciaResponseDTO dto = new EvidenciaResponseDTO();
        if (evidencia.isPresent()) {
            String baseImagen = UploadUtil.getFileBase64(evidencia.get().getRutaImagen());

            dto.setIdEvidencia(evidencia.get().getIdEvidencia());
            dto.setDescripcion(evidencia.get().getDescripcion());
            dto.setFechaCreacion(evidencia.get().getFechaCreacion());
            dto.setImagenBase64(baseImagen);
            dto.setExtension(evidencia.get().getExtension());
            dto.setPorcentajeAvance(evidencia.get().getPorcentajeAvance());
        }
        return dto;
    }

    @Override
    public List<Actividad> listarAllPoi() {
        return tareaRepository.listarAllPoi();
    }



}
