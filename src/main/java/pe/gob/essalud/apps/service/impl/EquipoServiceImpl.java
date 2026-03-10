package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.dto.auth.UserSessionDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.CargaMasivaVotanteDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.request.UpdateVotanteDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.TrabajadorResponseDto;
import pe.gob.essalud.apps.dto.gestionrendimiento.response.VotantePlanillaResponseDto;
import pe.gob.essalud.apps.exceptions.ValidationException;
import pe.gob.essalud.apps.model.miessalud.UnidadOrganizativa;
import pe.gob.essalud.apps.model.miessalud.Usuario;
import pe.gob.essalud.apps.model.miessalud.Votante;
import pe.gob.essalud.apps.model.miessalud.gestionrendimiento.Equipo;
import pe.gob.essalud.apps.repository.miessalud.UnidadOrganizativaRepository;
import pe.gob.essalud.apps.repository.miessalud.UsuarioRepository;
import pe.gob.essalud.apps.repository.miessalud.VotanteRepository;
import pe.gob.essalud.apps.repository.miessalud.gestionrendimiento.EquipoRepository;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.EquipoService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
//@Slf4j
public class EquipoServiceImpl implements EquipoService {

    private final EquipoRepository equipoRepository;
    private final VotanteRepository votanteRepository;
    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;
    private final UnidadOrganizativaRepository unidadOrganizativaRepository;

    @Override
    public void registrarTrabajador(Equipo equipo) {
        if (equipo != null) {
            Votante getJefeVotante = equipoRepository.getVotanteByIdUsuario(authService.getIdUserSession());

            if (getJefeVotante != null) {
                Votante votante = new Votante();
                votante.setIdVotante(getJefeVotante.getIdVotante());
                equipo.setJefe(votante);
                equipo.setEsActivo(true);
                equipo.setUsuarioCreacion(authService.getIdUserSession());

                //se trae toda la lista de equipo para buscar el evaluador
                List<Equipo> listEquipo = equipoRepository.getListTrabajadoresByIdUsuarioJefeOrEvaluador(getJefeVotante.getIdVotante());
                equipo.setEvaluador(listEquipo.stream()
                        .map(Equipo::getEvaluador)
                        .filter(evaluador -> evaluador != null)
                        .findFirst()
                        .orElse(null));
            } else {
                throw new ValidationException("No puede registrar trabajador porque no cuenta con usuario en votantes");
            }
        }
        Equipo model = equipoRepository.save(equipo);
    }

    @Override
    public void registrarEvaluador(Equipo equipo) {
        if (equipo != null) {
            Votante getJefeVotante = equipoRepository.getVotanteByIdUsuario(authService.getIdUserSession());

            if (getJefeVotante != null) {
                Votante votante = new Votante();
                votante.setIdVotante(getJefeVotante.getIdVotante());
                equipo.setJefe(votante);
                equipo.setEsActivo(true);
                equipo.setUsuarioCreacion(authService.getIdUserSession());
            } else {
                throw new ValidationException("No puede registrar evaluador porque no cuenta con usuario en votantes");
            }
        }
        assert equipo != null;
        Integer respt = equipoRepository.actualizarEvaluador( equipo.getIntegrante().getIdVotante(), equipo.getJefe().getIdVotante());
    }

    @Override
    public List<Equipo> getListTrabajadoresByIdUsuarioJefe() {
        Votante votante = equipoRepository.getVotanteByIdUsuario(authService.getIdUserSession());
        return equipoRepository.getListTrabajadoresByIdUsuarioJefeOrEvaluador(votante.getIdVotante());
//        return equipoRepository.getListTrabajadoresByIdUsuarioJefe(authService.getIdUserSession());
    }
    @Override
    public String getListEvaluadorByIdUsuarioJefe() {
        Votante jefe = equipoRepository.getVotanteByIdUsuario(authService.getIdUserSession());
        Integer idVotanteEvaluador = equipoRepository.getIdVotanteEvaluador(jefe.getIdVotante());
        if (idVotanteEvaluador!=null){
            Votante evaluador = equipoRepository.getVotanteByIdVotante(idVotanteEvaluador);
            return evaluador.getNumeroDocumento()+" "+evaluador.getNombres()+" "+evaluador.getApellidos();
        }else{
            return null;
        }
    }
    @Override
    public Boolean verificaEvaluadorByIdUsuarioJefe() {
        Votante jefe = equipoRepository.getVotanteByIdUsuario(authService.getIdUserSession());
        Integer idVotanteEvaluador = equipoRepository.getIdVotanteEvaluador(jefe.getIdVotante());
        if (idVotanteEvaluador!=null){
            if (idVotanteEvaluador.equals(jefe.getIdVotante())){
                return Boolean.TRUE;
            }else{
                return Boolean.FALSE;
            }
        }else{
            return Boolean.FALSE;
        }
    }

    @Override
    public int eliminarTrabajador(Number idEquipo) {
        return equipoRepository.eliminarTrabajador(idEquipo);
    }

    @Override
    public List<TrabajadorResponseDto> listAllVotante() {
        return equipoRepository.listAllVotante(authService.getIdUserSession());
    }

    @Override
    public Votante getVotanteByIdUsuario() {
        Votante votante = equipoRepository.getVotanteByIdUsuario(authService.getIdUserSession());
        if (votante!= null && votante.getIdSegmento().equals(3)){
            Integer esEvaluador = equipoRepository.getEsEvaluadorDelGrupo(votante.getIdVotante());
            if (esEvaluador>0){
                votante.setIdSegmento(5);
            }
        }
        return votante;
    }

    @Override
    public List<Votante> findVotanteByNombre(String nombre) {
        return equipoRepository.findVotanteByNombre(nombre);
    }
    


    @Override
    public List<VotantePlanillaResponseDto> findVotanteByNombre2(String nombre) {
        return equipoRepository.findVotanteByNombre2(nombre);
    }

    @Override
    public List<Votante> findAllVotantePerfil() {
        return votanteRepository.findAll();
    }

    @Override
    public void modificarPerfilVotante(int id, UpdateVotanteDto request) {
        Votante votante = votanteRepository.findById(id)
                .orElseThrow(() -> new ValidationException("El votante no se encuentra"));

        votante.setIdSegmento(request.getIdSegmento());
        votanteRepository.save(votante);
    }

    @Override
    public List<CargaMasivaVotanteDto> cargaMasivaVotante(List<CargaMasivaVotanteDto> listVotantes) {
        List<CargaMasivaVotanteDto> listObservados = new ArrayList<>();
        if (listVotantes != null) {
            for (CargaMasivaVotanteDto c : listVotantes) {
                Optional<Votante> votanteEncontrado = votanteRepository.findByNumeroDocumento(c.getNumeroDocumento());

                if (votanteEncontrado.isPresent()) {
//                    log.info("votanteEncontrado [{}]", votanteEncontrado.get().getNumeroDocumento());
                }else{
                    Usuario findUsuario = usuarioRepository.findDocumento(c.getNumeroDocumento());
//                    log.info("findUsuario [{}]", findUsuario);

                    if (findUsuario != null) {
                        int maxVotante = equipoRepository.getCantidadRegistro();
//                        log.info("idVotante [{}-{}]", maxVotante, maxVotante+1);

                        Votante model = new Votante();
                        model.setIdVotante(maxVotante+1);
                        model.setNumeroDocumento(c.getNumeroDocumento());
                        model.setNombres(c.getNombres());
                        model.setApellidos(c.getApellidos());
                        model.setIdSegmento(c.getIdSegmento());
                        model.setIdUsuario((int)findUsuario.getIdUsuario());
                        votanteRepository.save(model);
                    } else {
//                        log.info("No registrado en votante porque no se encuentra en usuarios [{}]", c.getNumeroDocumento());

                        CargaMasivaVotanteDto observado = new CargaMasivaVotanteDto();
                        observado.setNumeroDocumento(c.getNumeroDocumento());
                        observado.setNombres(c.getNombres());
                        observado.setApellidos(c.getApellidos());

                        listObservados.add(observado);
                    }
                }

            }
        }
        return listObservados;
    }

    @Override
    public Usuario findUsuarioSctrByNumeroDocumento(String numDoc) {
        return usuarioRepository.findDocumento(numDoc);
    }

    @Override
    public List<TrabajadorResponseDto> listAllVotanteByUnidadOrganizativa() {
        UserSessionDto userSessionDto = authService.getUserSession();
        String codUnidad = userSessionDto.getCodUnidad();
        UnidadOrganizativa unidadOrganizativa = unidadOrganizativaRepository.findFirstByCodUnidad(codUnidad);
        if (unidadOrganizativa.getCodPadre() == null){
            List<UnidadOrganizativa> listadoUnidades = unidadOrganizativaRepository.findAllByCodPadre(unidadOrganizativa.getCodUnidad());
            List<String> codigosDeUnidades = new ArrayList<>();
            listadoUnidades.forEach(und -> codigosDeUnidades.add(und.getCodUnidad()));
            codigosDeUnidades.add(unidadOrganizativa.getCodUnidad());
            return equipoRepository.listAllVotanteByCodUnidad(authService.getIdUserSession(), codigosDeUnidades);
        }else{
            List<String> codigosDeUnidades = new ArrayList<>();
            codigosDeUnidades.add(unidadOrganizativa.getCodUnidad());
            return equipoRepository.listAllVotanteByCodUnidad(authService.getIdUserSession(), codigosDeUnidades);
        }
    }

    public List<TrabajadorResponseDto> listAllAvalibleEvaluador() {
        UserSessionDto userSessionDto = authService.getUserSession();
        String codUnidad = userSessionDto.getCodUnidad();
        UnidadOrganizativa unidadOrganizativa = unidadOrganizativaRepository.findFirstByCodUnidad(codUnidad);
        if (unidadOrganizativa.getCodPadre() == null){
            List<UnidadOrganizativa> listadoUnidades = unidadOrganizativaRepository.findAllByCodPadre(unidadOrganizativa.getCodUnidad());
            List<String> codigosDeUnidades = new ArrayList<>();
            listadoUnidades.forEach(und -> codigosDeUnidades.add(und.getCodUnidad()));
            codigosDeUnidades.add(unidadOrganizativa.getCodUnidad());
            return equipoRepository.listAllAvalibleEvaluador(codigosDeUnidades);
        }else{
            List<String> codigosDeUnidades = new ArrayList<>();
            codigosDeUnidades.add(unidadOrganizativa.getCodUnidad());
            return equipoRepository.listAllAvalibleEvaluador(codigosDeUnidades);
        }
    }

}
