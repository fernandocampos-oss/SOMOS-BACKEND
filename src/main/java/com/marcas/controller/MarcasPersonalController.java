package com.marcas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.marcas.model.projection.PersonalProjection;
import com.marcas.service.IMarcasPersonalService;


@RestController
@RequestMapping("/api")
public class MarcasPersonalController {
	
	@Autowired
	private IMarcasPersonalService marcasPersonalService;
	
	@GetMapping("/lista")
	public List<PersonalProjection> index(@RequestParam String desde, @RequestParam String hasta, @RequestParam String codigo) {
		return marcasPersonalService.findAllMarcas(desde, hasta, codigo);
	}
	

	@GetMapping("/mess")
	public String hellow() {
		return "hello word";
	}
}
