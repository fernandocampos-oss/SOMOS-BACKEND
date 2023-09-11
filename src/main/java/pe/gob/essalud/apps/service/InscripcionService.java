package pe.gob.essalud.apps.service;


import pe.gob.essalud.apps.dto.inscripcion.response.InscripcionResponseDto;

public interface InscripcionService {

     InscripcionResponseDto buscarInscripcionPorId(int idInscripcion);

     void guardarInscripcion(int idInscripcion);
}
