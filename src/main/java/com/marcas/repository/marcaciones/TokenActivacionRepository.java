package com.marcas.repository.marcaciones;

import com.marcas.model.marcaciones.TokenActivacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenActivacionRepository extends JpaRepository<TokenActivacion, Long> {

    Optional<TokenActivacion> findTopByIdUsuarioOrderByFechaCreacionDesc(long idUsuario);
    Optional<TokenActivacion> findTopByIdUsuarioAndTokenOrderByFechaCreacionDesc(long idUsuario, String token);

}
