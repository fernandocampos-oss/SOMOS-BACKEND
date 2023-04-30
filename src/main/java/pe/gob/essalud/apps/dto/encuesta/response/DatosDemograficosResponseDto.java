package pe.gob.essalud.apps.dto.encuesta.response;

import lombok.Data;

import java.util.List;

@Data
public class DatosDemograficosResponseDto {

    private List<AreaPersonalResponseDto> areasPersonales;
    private List<GrupoPersonalResponseDto> gruposPersonales;
    private List<SedeResponseDto> sedes;
    private List<TiempoServicioResponseDto> tiemposDeServicios;

}
