package pe.gob.essalud.apps.dto.gestionrendimiento.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CargaMasivaEvaluadorRequestDto {
    private List<String> dnis;
}
