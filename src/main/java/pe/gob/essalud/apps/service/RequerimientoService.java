package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Requerimiento;

public interface RequerimientoService extends IcrudService<Requerimiento> {

    void modificarRequerimiento(Integer idRequerimiento, Requerimiento request);

}
