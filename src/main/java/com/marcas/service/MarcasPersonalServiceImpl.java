package com.marcas.service;

import java.util.List;

import com.marcas.model.marcaciones.Onomastico;
import com.marcas.repository.marcaciones.OnomasticoRepository;
import org.springframework.stereotype.Service;

import com.marcas.model.tweb2.projection.PersonalProjection;
import com.marcas.repository.tweb2.IMarcasPersonalRepository;

@Service
public class MarcasPersonalServiceImpl implements MarcasPersonalService {

	private final IMarcasPersonalRepository ambitoRep;
	private final OnomasticoRepository onomasticoRepository;

	public MarcasPersonalServiceImpl(IMarcasPersonalRepository ambitoRep, OnomasticoRepository onomasticoRepository) {
		this.ambitoRep = ambitoRep;
		this.onomasticoRepository = onomasticoRepository;
	}

	@Override
	public List<PersonalProjection> findAllMarcas(String desde, String hasta, String codigo) {
		return ambitoRep.findAllMarcas(desde,hasta,codigo);
	}

	@Override
	public List<Onomastico> findAllOnomasticos() {
		return onomasticoRepository.findAll();
	}

	@Override
	public List<Onomastico> findAllOnomasticosByMes(String mes) {
		return onomasticoRepository.findByMes(mes);
	}

	@Override
	public List<Onomastico> findAllOnomasticosByMesAndDia(String mes, String dia) {
		return onomasticoRepository.findByMesAndDia(mes, dia);
	}


}
