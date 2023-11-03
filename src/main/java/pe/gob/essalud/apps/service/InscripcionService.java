package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.inscripcion.request.InscripcionRequestDto;
import pe.gob.essalud.apps.dto.inscripcion.request.InscripcionVotoRequestDto;
import pe.gob.essalud.apps.dto.inscripcion.response.InscripcionDatosResponseDto;
import pe.gob.essalud.apps.dto.inscripcion.response.InscripcionResponseDto;
import pe.gob.essalud.apps.dto.inscripcion.response.InscripcionVotacionResponseDto;
import pe.gob.essalud.apps.dto.inscripcion.response.ReporteInscritosDto;

import java.util.List;

public interface InscripcionService {

     InscripcionResponseDto buscarInscripcionPorId(int idInscripcion);

     InscripcionDatosResponseDto buscarDatosInscripcionPorId(int idInscripcion);

     void guardarInscripcion(InscripcionRequestDto request);

     ReporteInscritosDto getUsuariosInscritos(int idInscripcion);

     List<InscripcionVotacionResponseDto> listarVotacionesActivas();

     void guardarVoto(InscripcionVotoRequestDto votoRequestDto);

     void activarVotacion(int idInscripcion, boolean votoActivo);
}
