package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.dto.material.MaestroMaterialSAP;
import pe.gob.essalud.apps.dto.material.StockMaterialSAP;

import java.util.List;

public interface MaterialService {

    List<MaestroMaterialSAP> buscarMaestroMaterial(String nombreMaterial);
    List<StockMaterialSAP> buscarStockMaterial(String codigoMaterial, String codigoRed);

}
