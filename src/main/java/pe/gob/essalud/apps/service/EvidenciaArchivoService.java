package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.EvidenciaArchivo;

public interface EvidenciaArchivoService extends IcrudService<EvidenciaArchivo> {

    EvidenciaArchivo listarArchivoPorEstadoActivo(Number idEvidenciaArchivo);

    int eliminarArchivo(Boolean estado, Number idEvidenciaArchivo);
}
