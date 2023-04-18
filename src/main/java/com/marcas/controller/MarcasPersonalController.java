package com.marcas.controller;

import java.util.List;

import com.marcas.model.marcaciones.Onomastico;
import org.springframework.web.bind.annotation.*;

import com.marcas.model.tweb2.projection.PersonalProjection;
import com.marcas.service.IMarcasPersonalService;


@RestController
@RequestMapping("/api")
public class MarcasPersonalController {
	
	private final IMarcasPersonalService marcasPersonalService;

	public MarcasPersonalController(IMarcasPersonalService marcasPersonalService) {
		this.marcasPersonalService = marcasPersonalService;
	}

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
