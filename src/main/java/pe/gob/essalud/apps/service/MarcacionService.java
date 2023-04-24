package pe.gob.essalud.apps.service;

import java.util.List;

import pe.gob.essalud.apps.model.tempus.projection.PersonalProjection;


public interface MarcacionService {

	List<PersonalProjection> findAllMarcas(String desde, String hasta, String codigo);

}
