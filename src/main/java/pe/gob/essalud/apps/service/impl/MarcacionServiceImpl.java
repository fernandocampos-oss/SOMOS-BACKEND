package pe.gob.essalud.apps.service.impl;

import java.util.List;

import lombok.RequiredArgsConstructor;
import pe.gob.essalud.apps.client.MarcacionConsServiceClient;
import pe.gob.essalud.apps.dto.marcacioncons.PersonalProjection;
import pe.gob.essalud.apps.service.MarcacionService;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MarcacionServiceImpl implements MarcacionService {

	private final MarcacionConsServiceClient _marcacionConsServiceClient;

	@Override
	public List<PersonalProjection> findAllMarcas(String desde, String hasta, String codigo) {
		return _marcacionConsServiceClient.findAllMarcas(desde,hasta,codigo);
	}

}
