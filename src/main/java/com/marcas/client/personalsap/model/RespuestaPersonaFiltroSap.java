package com.marcas.client.personalsap.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "OT_DATA")
public class RespuestaPersonaFiltroSap {
    List<PersonaFiltroSAP> data;

    @XmlElement(name = "item")
    public List<PersonaFiltroSAP> getData() {
        return data;
    }

    public void setData(List<PersonaFiltroSAP> data) {
        this.data = data;
    }
}
