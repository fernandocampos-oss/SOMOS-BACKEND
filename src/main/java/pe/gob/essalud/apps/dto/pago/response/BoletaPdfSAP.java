package pe.gob.essalud.apps.dto.pago.response;

import lombok.Data;

import java.util.List;

@Data
public class BoletaPdfSAP {

    private List<BoletaSAP> boleta;
    private List<PdfSAP> pdf;

}
