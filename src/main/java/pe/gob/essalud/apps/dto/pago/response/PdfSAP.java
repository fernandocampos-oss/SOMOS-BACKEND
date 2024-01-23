package pe.gob.essalud.apps.dto.pago.response;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class PdfSAP {

    @XmlElement(name = "Line", required = true)
    protected String lineaPdfBase64;

}
