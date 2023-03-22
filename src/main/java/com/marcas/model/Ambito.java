package com.marcas.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "TEMPUS.AMBITO") 
public class Ambito {
	
	@Id
	int idAmbito;
	String descAmbito;
	String publico;
	int idAutorizacioncrea;
	int idAmbitopadre;
	int tipoAmbito;
	
}
