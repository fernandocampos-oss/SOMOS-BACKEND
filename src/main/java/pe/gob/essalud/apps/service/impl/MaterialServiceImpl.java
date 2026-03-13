package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.client.MaterialSapServiceClient;
import pe.gob.essalud.apps.dto.material.MaestroMaterialSAP;
import pe.gob.essalud.apps.dto.material.StockMaterialSAP;
import pe.gob.essalud.apps.service.MaterialService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    private final MaterialSapServiceClient materialSapServiceClient;

    @Override
    public List<MaestroMaterialSAP> buscarMaestroMaterial(String nombreMaterial) {
        return materialSapServiceClient.getMaestroMaterialSAP(nombreMaterial);
    }

    @Override
    public List<StockMaterialSAP> buscarStockMaterial(String codigoMaterial, String codigoRed) {
        return materialSapServiceClient.getStockMaterialSAP(codigoMaterial, codigoRed);
    }
}
