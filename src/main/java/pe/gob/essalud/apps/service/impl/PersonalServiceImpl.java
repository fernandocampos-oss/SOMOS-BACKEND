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
    public List<Personal> listarPersonalPorDependenciaAsignado(Number idDependencia, Character estadoAsignado) {
        return personalRepository.listarPersonalPorDependenciaAsignado(idDependencia, estadoAsignado);
    }

    @Override
    public List<Personal> buscarPersonalPorNombre(PersonalFiltroNombreDTO filtro) {
        return personalRepository.buscarPersonalPorNombre(filtro.getNombres());
    }

}