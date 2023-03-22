package com.marcas.service;

import java.util.List;

import com.marcas.model.projection.PersonalProjection;


public interface IMarcasPersonalService {

	public List<PersonalProjection> findAllMarcas(String desde, String hasta, String codigo);
}
