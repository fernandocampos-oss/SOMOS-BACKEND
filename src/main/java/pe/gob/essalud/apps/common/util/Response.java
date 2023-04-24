package pe.gob.essalud.apps.common.util;

import lombok.Data;

@Data
public class Response {

    private int codigo;
    private Object data;
    private String msj;

}
