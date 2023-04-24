package pe.gob.essalud.apps.controller;

import java.util.List;

import pe.gob.essalud.apps.base.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import pe.gob.essalud.apps.model.tempus.projection.PersonalProjection;
import pe.gob.essalud.apps.service.MarcacionService;


@RestController
@RequestMapping(MarcacionController.MARCACION)
@PreAuthorize("authenticated")
@RequiredArgsConstructor
public class MarcacionController extends BaseController {

	static final String MARCACION = "marcaciones";
	private final MarcacionService marcacionService;

	@GetMapping
	public List<PersonalProjection> findAllMarcas(@RequestParam String desde, @RequestParam String hasta, @RequestParam String codigo) {
		return marcacionService.findAllMarcas(desde, hasta, codigo);
	}

}
