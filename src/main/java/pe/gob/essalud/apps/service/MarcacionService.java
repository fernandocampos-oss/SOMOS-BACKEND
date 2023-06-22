package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.marcacioncons.PersonalProjection;

import java.util.List;



public interface MarcacionService {

	List<PersonalProjection> findAllMarcas(String desde, String hasta, String codigo);

}
