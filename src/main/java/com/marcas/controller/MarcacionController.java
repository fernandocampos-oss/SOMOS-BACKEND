package com.marcas.controller;

import java.util.List;

import com.marcas.base.BaseController;
import com.marcas.model.marcaciones.Onomastico;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.marcas.model.tweb2.projection.PersonalProjection;
import com.marcas.service.MarcasPersonalService;


@RestController
@RequestMapping(MarcacionController.MARCACION)
@PreAuthorize("authenticated")
@RequiredArgsConstructor
public class MarcacionController extends BaseController {

	static final String MARCACION = "marcaciones";
	private final MarcasPersonalService marcasPersonalService;

	@GetMapping("/lista")
	public List<PersonalProjection> findAllMarcas(@RequestParam String desde, @RequestParam String hasta, @RequestParam String codigo) {
		return marcasPersonalService.findAllMarcas(desde, hasta, codigo);
	}

	@GetMapping("/onomasticos")
	public List<Onomastico> findAllOnomasticos() {
		return marcasPersonalService.findAllOnomasticos();
	}

	@GetMapping("/onomasticos/mes/{mes}")
	public List<Onomastico> findAllOnomasticosByMes(@PathVariable String mes) {
		return marcasPersonalService.findAllOnomasticosByMes(mes);
	}

	@GetMapping("/onomasticos/mes/{mes}/dia/{dia}")
	public List<Onomastico> findAllOnomasticosByMesAndDia(@PathVariable String mes, @PathVariable String dia) {
		return marcasPersonalService.findAllOnomasticosByMesAndDia(mes, dia);
	}

}
