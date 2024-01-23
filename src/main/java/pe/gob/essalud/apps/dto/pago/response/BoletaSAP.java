package pe.gob.essalud.apps.dto.pago.response;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class BoletaSAP {

    @XmlElement(name = "Pernr", required = true)
    protected String codigoPlanilla;
    @XmlElement(name = "Femis", required = true)
    protected String fechaCreacion;
    @XmlElement(name = "Ptext", required = true)
    protected String grupo;
    @XmlElement(name = "Name1", required = true)
    protected String division;
    @XmlElement(name = "Orgtx", required = true)
    protected String objeto;
    @XmlElement(name = "Tipbo", required = true)
    protected String tipoBoleta;

}
