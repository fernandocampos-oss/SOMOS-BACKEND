package pe.gob.essalud.apps.service;

import pe.gob.essalud.apps.model.miessalud.Reglamento;

import java.util.List;

public interface ReglamentoService {
    Reglamento getReglamentoBySemestre(String numDoc, int semestre, int anio);
    List<Reglamento> getAll();
    void save(Reglamento model);

}
