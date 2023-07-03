package pe.gob.essalud.apps.service.impl;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import pe.gob.essalud.apps.dto.gestionrendimiento.PersonalFiltroNombreDTO;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Personal;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.PersonalRepository;
import pe.gob.essalud.apps.service.PersonalService;

@Service
@RequiredArgsConstructor
public class PersonalServiceImpl implements PersonalService {

    private final PersonalRepository personalRepository;

    @Override
    public List<Personal> listar() {
        return personalRepository.findAll();
    }

    @Override
    public Personal registrar(Personal obj) {
        return personalRepository.save(obj);
    }

    @Override
    public List<Personal> listarPersonalPorDependenciaAsignado(Number idDependencia, Number idEstadoPersonal) {
        return personalRepository.listarPersonalPorDependenciaAsignado(idDependencia, idEstadoPersonal);
    }

    @Override
    public List<Personal> buscarPersonalPorNombre(PersonalFiltroNombreDTO filtro) {
        return personalRepository.buscarPersonalPorNombre(filtro.getNombres());
    }
    @Override
    public int eliminarPersonalMotivo(Number idEstadoPersonal, String motivo, Number idPersonal) {
        return personalRepository.eliminarPersonalMotivo(idEstadoPersonal, motivo, idPersonal);
    }

}