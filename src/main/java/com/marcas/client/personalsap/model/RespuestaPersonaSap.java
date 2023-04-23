package com.marcas.client.personalsap.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "OT_DATA")
public class RespuestaPersonaSap {
    List<PersonaSAP> data;

    @XmlElement(name = "item")
    public List<PersonaSAP> getData() {
        return data;
    }

    public void setData(List<PersonaSAP> data) {
        this.data = data;
    }
}
