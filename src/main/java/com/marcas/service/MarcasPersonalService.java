package com.marcas.service;

import java.util.List;

import com.marcas.model.marcaciones.Onomastico;
import com.marcas.model.tweb2.projection.PersonalProjection;


public interface MarcasPersonalService {

	List<PersonalProjection> findAllMarcas(String desde, String hasta, String codigo);
	List<Onomastico> findAllOnomasticos();
	List<Onomastico> findAllOnomasticosByMes(String mes);
	List<Onomastico> findAllOnomasticosByMesAndDia(String mes, String dia);

}
