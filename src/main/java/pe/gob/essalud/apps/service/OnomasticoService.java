package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.onomastico.response.OnomasticoResponseDto;
import pe.gob.essalud.apps.model.miessalud.Onomastico;
import pe.gob.essalud.apps.model.miessalud.Usuario;

import java.util.List;

public interface OnomasticoService {

    List<Onomastico> findAllOnomasticos();
    List<Onomastico> findAllOnomasticosByMes(String mes);
//    List<Onomastico> findAllOnomasticosByMesAndDia(String mes, String dia);
    List<OnomasticoResponseDto> getOnomasticosByMesAndDiaAndEstado(String mes, String dia);
    Usuario findUsuarioByNumDocAndEstado(String numeroDocumento);
    List<OnomasticoResponseDto> obtenerOnomasticosPorDiaAndEstado(String mes, String dia);
}
