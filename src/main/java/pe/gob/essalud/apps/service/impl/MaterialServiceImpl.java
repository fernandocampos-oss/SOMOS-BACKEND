package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.client.MaterialSapServiceClient;
import pe.gob.essalud.apps.dto.material.MaestroMaterialSAP;
import pe.gob.essalud.apps.dto.material.StockMaterialSAP;
import pe.gob.essalud.apps.service.MaterialService;
import pe.gob.essalud.apps.service.UsuarioService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    private final MaterialSapServiceClient materialSapServiceClient;
    private final UsuarioService usuarioService;

    @Override
    public List<MaestroMaterialSAP> buscarMaestroMaterial(String nombreMaterial) {
        if (usuarioService.usuarioTienePermisoModulo("MATERIALES")) {
            return materialSapServiceClient.getMaestroMaterialSAP(nombreMaterial);
        }
        return List.of();
    }

    @Override
    public List<StockMaterialSAP> buscarStockMaterial(String codigoMaterial, String codigoRed) {
        if (usuarioService.usuarioTienePermisoModulo("MATERIALES")) {
            return materialSapServiceClient.getStockMaterialSAP(codigoMaterial, codigoRed);
        }
        return List.of();
    }
}
