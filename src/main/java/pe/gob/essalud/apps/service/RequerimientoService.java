package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Requerimiento;
import pe.gob.essalud.apps.service.IcrudService;

public interface RequerimientoService extends IcrudService<Requerimiento> {

    int aprobarRequerimiento(Number estado, Number idRequerimiento);

    int rechazarRequerimiento(Number estado, String motivo, Number idRequerimiento);

    int derivarRequerimiento(Number estado, String motivo, Number idAreaReceptor, Number idRequerimiento);

}
