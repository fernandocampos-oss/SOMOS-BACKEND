package com.marcas.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marcas.model.projection.PersonalProjection;
import com.marcas.repository.IMarcasPersonalRepository;

@Service
public class MarcasPersonalService implements IMarcasPersonalService{

	@Autowired
	private IMarcasPersonalRepository ambitoRep;
		
	@Override
	public List<PersonalProjection> findAllMarcas(String desde, String hasta, String codigo) {
		return ambitoRep.findAllMarcas(desde,hasta,codigo);
	}

	
	
}
