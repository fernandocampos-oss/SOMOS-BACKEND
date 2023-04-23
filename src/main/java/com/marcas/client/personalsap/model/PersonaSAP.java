package com.marcas.client.personalsap.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class PersonaSAP {
	private Integer item;
    @XmlElement(name = "PERNR", required = true)
    protected String codPlanilla;
    @XmlElement(name = "ENAME", required = true)
    protected String nombres;
    @XmlElement(name = "PERSG", required = true)
    protected String regimenCod;
    @XmlElement(name = "PTEXT", required = true)
    protected String regimen;
    @XmlElement(name = "ABKRS", required = true)
    protected String abkrs;
    @XmlElement(name = "STELL", required = true)
    protected String stell;
    @XmlElement(name = "STLTX", required = true)
    protected String cargo;
    @XmlElement(name = "IDNUM", required = true)
    protected String numeroDni;
    @XmlElement(name = "BEGDA", required = true)
    protected String fechaNac;
    @XmlElement(name = "CTTYP", required = true)
    protected String cttyp;
    @XmlElement(name = "CTTXT", required = true)
    protected String cttxt;
    @XmlElement(name = "VDSK1", required = true)
    protected String vdsk1;
    @XmlElement(name = "GESCH", required = true)
    protected String gesch;
    @XmlElement(name = "SEXOT", required = true)
    protected String sexot;
    @XmlElement(name = "WERKS", required = true)
    protected String werks;
    @XmlElement(name = "GSBER", required = true)
    protected String gsber;
    @XmlElement(name = "ORGEH", required = true)
    protected String orgeh;
    @XmlElement(name = "KTEXT")
    protected String profesion;
}
