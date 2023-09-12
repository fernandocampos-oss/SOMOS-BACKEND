package pe.gob.essalud.apps.service;


import pe.gob.essalud.apps.dto.inscripcion.response.InscripcionResponseDto;
import pe.gob.essalud.apps.dto.inscripcion.response.ReporteInscritosDto;
import pe.gob.essalud.apps.dto.inscripcion.response.UsuariosInscritosResponseDto;

import java.util.List;

public interface InscripcionService {

     InscripcionResponseDto buscarInscripcionPorId(int idInscripcion);

     void guardarInscripcion(int idInscripcion);

     ReporteInscritosDto getUsuariosInscritos(int idInscripcion);
}
