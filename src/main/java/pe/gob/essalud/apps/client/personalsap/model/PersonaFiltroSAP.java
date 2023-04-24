package pe.gob.essalud.apps.client.personalsap.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class PersonaFiltroSAP {
	private Integer item;
    @XmlElement(name = "ENAME", required = true)
    protected String nombres;
    @XmlElement(name = "IDNUM", required = true)
    protected String numeroDni;
    
    @XmlElement(name = "STLTX", required = true)
    protected String cargo;
    
    @XmlElement(name = "KTEXT", required = true)
    protected String profesion;

    private Integer idModalidad;
    private Integer idRiesgo;
}
