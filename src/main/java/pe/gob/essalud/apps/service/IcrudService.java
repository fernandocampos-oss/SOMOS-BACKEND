package pe.gob.essalud.apps.service;

import java.util.List;

public interface IcrudService<T> {

    T registrar(T t);
//    T modificar(T t);
//    T listarPorId(Integer id);
    List<T> listar();
//    void eliminar(Integer id);

}
