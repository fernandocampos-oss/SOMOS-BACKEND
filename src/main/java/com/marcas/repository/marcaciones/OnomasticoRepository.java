package com.marcas.repository.marcaciones;

import com.marcas.model.marcaciones.Onomastico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OnomasticoRepository extends JpaRepository<Onomastico, Integer> {

    List<Onomastico> findByMes(String mes);
    List<Onomastico> findByMesAndDia(String mes, String dia);

}
