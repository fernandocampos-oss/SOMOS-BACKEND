package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.exceptions.ValidationException;
import pe.gob.essalud.apps.model.miessalud.RedPersonal;
import pe.gob.essalud.apps.model.miessalud.Reglamento;
import pe.gob.essalud.apps.model.miessalud.UnidadOrganizativa;
import pe.gob.essalud.apps.model.miessalud.Usuario;
import pe.gob.essalud.apps.repository.miessalud.RedPersonalRepository;
import pe.gob.essalud.apps.repository.miessalud.ReglamentoRepository;
import pe.gob.essalud.apps.repository.miessalud.UnidadOrganizativaRepository;
import pe.gob.essalud.apps.repository.miessalud.UsuarioRepository;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.ReglamentoService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReglamentoServiceImpl implements ReglamentoService {
    private final ReglamentoRepository reglamentoRepository;
    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;
    private final RedPersonalRepository redPersonalRepository;
    private final UnidadOrganizativaRepository unidadOrganizativaRepository;

    @Override
    public Reglamento getReglamentoBySemestre(String numDoc, int semestre, int anio) {
        Reglamento result = new Reglamento();
        if (semestre == 1) {
            result = reglamentoRepository.getReglamentoByPrimerSemestreAnio(numDoc, anio);
        }
        if (semestre == 2) {
            result = reglamentoRepository.getReglamentoBySegundoSemestreAnio(numDoc, anio);
        }
        return result;
    }

    @Override
    public List<Reglamento> getAll() {
        return reglamentoRepository.findAll();
    }

    @Override
    public void save(Reglamento model) {
        Usuario usuario = usuarioRepository.findById((long) authService.getIdUserSession()).orElseThrow(() -> new ValidationException("El usuario no existe"));
        RedPersonal red = new RedPersonal();
        UnidadOrganizativa unidad = new UnidadOrganizativa();
        if (usuario.getCodigoRed() != null) {
            red = redPersonalRepository.findByCodRed(usuario.getCodigoRed());
        }
        if (usuario.getCodigoUnidad() != null) {
            unidad = unidadOrganizativaRepository.findFirstByCodUnidad(usuario.getCodigoUnidad());
        }
        model.setIdUsuario(authService.getIdUserSession());
        model.setCodigoPlanilla(usuario.getCodigoPlanilla());
        model.setNumeroDocumento(usuario.getNumeroDocumento());
        model.setNombres(authService.getUserSession().getNombres());
        model.setApellidos(usuario.getApellidos());
        model.setPrimerSemestre(model.getPrimerSemestre());
        model.setSegundoSemestre(model.getSegundoSemestre());
        model.setAnio(LocalDateTime.now(ZoneId.of("America/Lima")).getYear());
        if (red.getCodRed() != null) {
            model.setRed(red.getDescripcion());
        }
        if (unidad.getCodUnidad() != null) {
            model.setUnidad(unidad.getDescripcion());
        }
        model.setFechaAperturaUsuario(usuario.getFechaCreacion());
        reglamentoRepository.save(model);
    }

}
