package pe.gob.essalud.apps.client.personalsap.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendMarcacionSapRequest {
    private String tipoMarcacion;
    private String codigoTerminal;
    private String codigoPlanilla;
    private LocalDateTime fechaMarcacion;
    private LocalDateTime fechaEnvio;
}
