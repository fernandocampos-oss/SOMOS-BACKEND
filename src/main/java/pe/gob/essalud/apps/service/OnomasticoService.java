package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.onomastico.response.IOnomasticoResponseDto;
import pe.gob.essalud.apps.model.miessalud.Onomastico;
import pe.gob.essalud.apps.model.miessalud.Usuario;

import java.util.List;

public interface OnomasticoService {

    List<Onomastico> findAllOnomasticos();
    List<Onomastico> findAllOnomasticosByMes(String mes);
//    List<Onomastico> findAllOnomasticosByMesAndDia(String mes, String dia);
    List<IOnomasticoResponseDto> obtenerOnomasticosInterfazPorDiaAndEstado(String mes, String dia);
    List<IOnomasticoResponseDto> obtenerOnomasticosCorreoPorDiaAndEstado(String mes, String dia);
    Usuario findUsuarioByNumDocAndEstado(String numeroDocumento);
}
