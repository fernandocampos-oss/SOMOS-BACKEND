package pe.gob.essalud.apps.service.impl;

import java.util.List;

import lombok.RequiredArgsConstructor;
import pe.gob.essalud.apps.service.MarcacionService;
import org.springframework.stereotype.Service;

import pe.gob.essalud.apps.model.tempus.projection.PersonalProjection;
import pe.gob.essalud.apps.repository.tempus.MarcacionRepository;

@Service
@RequiredArgsConstructor
public class MarcacionServiceImpl implements MarcacionService {

	private final MarcacionRepository marcacionRepository;

	@Override
	public List<PersonalProjection> findAllMarcas(String desde, String hasta, String codigo) {
		return marcacionRepository.findAllMarcas(desde,hasta,codigo);
	}

}
