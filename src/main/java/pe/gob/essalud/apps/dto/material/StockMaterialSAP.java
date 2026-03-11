package pe.gob.essalud.apps.dto.material;

import lombok.Data;

@Data
public class StockMaterialSAP {

    protected String codigoRed;
    protected String nombreRed;
    protected String codigoCentro;
    protected String nombreCentro;
    protected String codigoMaterial;
    protected String nombreMaterial;
    protected String medidaMaterial;
    protected String cantidadMaterial;

}
