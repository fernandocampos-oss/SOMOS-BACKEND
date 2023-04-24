package pe.gob.essalud.apps.service.impl;

import java.util.List;

import pe.gob.essalud.apps.service.MarcacionService;
import org.springframework.stereotype.Service;

import pe.gob.essalud.apps.model.tempus.projection.PersonalProjection;
import pe.gob.essalud.apps.repository.tempus.MarcacionRepository;

@Service
public class MarcacionServiceImpl implements MarcacionService {

	private final MarcacionRepository marcacionRepository;

	public MarcacionServiceImpl(MarcacionRepository marcacionRepository) {
		this.marcacionRepository = marcacionRepository;
	}

	@Override
	public List<PersonalProjection> findAllMarcas(String desde, String hasta, String codigo) {
		return marcacionRepository.findAllMarcas(desde,hasta,codigo);
	}

}
