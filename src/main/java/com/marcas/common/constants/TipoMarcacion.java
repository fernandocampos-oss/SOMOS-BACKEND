package com.marcas.common.constants;

public class TipoMarcacion {

    public static final String Entrada = "01";
    public static final String Salida = "02";

    public static String toTipoMarcacionSap(String idTipoMarcacion){
        switch (idTipoMarcacion){
            case Entrada:
                return TipoMarcacionSap.Entrada;
            case Salida:
                return TipoMarcacionSap.Salida;
            case "":
                return "";
            default:
                throw new IllegalArgumentException("El tipo de marcación no es válido");
        }
    }

}
