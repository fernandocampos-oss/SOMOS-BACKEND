package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.common.constants.RoleType;
import pe.gob.essalud.apps.dto.usuario.response.UsuarioNombresResponse;
import pe.gob.essalud.apps.dto.usuariored.request.UsuarioRedRequest;
import pe.gob.essalud.apps.dto.usuariored.response.RedResponse;
import pe.gob.essalud.apps.dto.usuariored.response.UsuarioRedResponse;
import pe.gob.essalud.apps.exceptions.ValidationException;
import pe.gob.essalud.apps.model.miessalud.RedPersonal;
import pe.gob.essalud.apps.model.miessalud.Usuario;
import pe.gob.essalud.apps.model.miessalud.UsuarioRed;
import pe.gob.essalud.apps.model.miessalud.UsuarioRedId;
import pe.gob.essalud.apps.repository.miessalud.PublicacionRepository;
import pe.gob.essalud.apps.repository.miessalud.RedPersonalRepository;
import pe.gob.essalud.apps.repository.miessalud.UsuarioRedRepository;
import pe.gob.essalud.apps.repository.miessalud.UsuarioRepository;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.UsuarioRedService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioRedServiceImpl implements UsuarioRedService {

    private final UsuarioRedRepository usuarioRedRepository;
    private final UsuarioRepository usuarioRepository;
    private final RedPersonalRepository redPersonalRepository;
    private final PublicacionRepository publicacionRepository;

    private final AuthService authService;
    private final ModelMapper modelMapper;

    @Override
    public List<UsuarioNombresResponse> listarAministradoresRed() {
        return usuarioRepository.findByIdRolIn(Arrays.asList(RoleType.TRABAJADOR, RoleType.ADMIN_SEDE, RoleType.ADMIN_CENTRAL)).stream()
                .map(u -> {
                    String nombres = Optional.ofNullable(u.getNombres()).orElse("");
                    String apellidos = Optional.ofNullable(u.getApellidos()).orElse("");
                    return UsuarioNombresResponse.builder()
                            .idUsuario(u.getIdUsuario())
                            .nombresCompletos(nombres + " " + apellidos)
                            .build();
                }).collect(Collectors.toList());
    }

    @Override
    public List<RedPersonal> listarRedes() {
        return redPersonalRepository.findAll();
    }

    @Override
    public List<UsuarioRedResponse> listarUsuariosRedes() {
        var usuarioRedList = usuarioRedRepository.findAllOrderByFechaCreacionDesc();

        return usuarioRedList.stream()
                .map(ur -> {
                    var response = new UsuarioRedResponse();
                    String nombres = Optional.ofNullable(ur.getUsuario().getNombres()).orElse("");
                    String apellidos = Optional.ofNullable(ur.getUsuario().getApellidos()).orElse("");
                    var usuarioNombre = UsuarioNombresResponse.builder()
                            .idUsuario(ur.getUsuario().getIdUsuario())
                            .nombresCompletos(nombres + " " + apellidos)
                            .build();
                    response.setUsuario(usuarioNombre);
                    response.setHabilitado(ur.isHabilitado());
                    response.setFechaAsignacion(ur.getFechaAsignacion());
                    response.setRed(modelMapper.map(ur.getRed(), RedResponse.class));
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<RedResponse> listarUsuarioRedesAsignadas() {
        var usuarioRedList = usuarioRedRepository.findByUsuarioIdUsuario(authService.getIdUserSession());

        return usuarioRedList.stream()
                .map(ur -> modelMapper.map(ur.getRed(), RedResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void asignarRedesUsuario(UsuarioRedRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new ValidationException("El usuario no se encuentra registrado"));

        if (usuario.getIdRol() == RoleType.TRABAJADOR) {
            usuario.setIdRol(RoleType.ADMIN_SEDE);
            usuario = usuarioRepository.save(usuario);
        } else if (usuario.getIdRol() == RoleType.ADMIN_CENTRAL && usuario.getIdRolAdicional() == null) {
            usuario.setIdRolAdicional(RoleType.ADMIN_SEDE);
            usuario = usuarioRepository.save(usuario);
        }

        asignarRedes(request, usuario);
    }

    @Override
    @Transactional
    public void actualizarRedesUsuario(UsuarioRedRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new ValidationException("El usuario no se encuentra registrado"));

        var usuarioRedList = usuarioRedRepository.findByUsuarioIdUsuario(usuario.getIdUsuario());
        List<String> redesNuevas = new ArrayList<>();
        for (UsuarioRed usuarioRed: usuarioRedList) {
            boolean redEliminada = true;
            for (String codRed: request.getRedes()) {
                if (usuarioRed.getRed().getCodRed().equals(codRed)) {
                    redEliminada = false;
                } else {
                    redesNuevas.add(codRed);
                }
            }
            if (redEliminada) {
                usuarioRed.setUsuarioModificacion(authService.getIdUserSession());
                usuarioRed.setEsActivo(false);
                usuarioRedRepository.save(usuarioRed);
            }
            request.setRedes(redesNuevas);
        }

        asignarRedes(request, usuario);
    }

    private void asignarRedes(UsuarioRedRequest request, Usuario usuario) {
        request.getRedes().forEach(codRed -> {
            RedPersonal red = redPersonalRepository.findById(codRed)
                    .orElseThrow(() -> new ValidationException("La red no se encuentra registrada"));

            Integer cant = usuarioRedRepository.buscarUsuarioRedInactivo(usuario.getIdUsuario(), red.getCodRed());
            if (cant > 0) {
                usuarioRedRepository.activarUsuarioRed(usuario.getIdUsuario(), red.getCodRed());
                UsuarioRed usuarioRed = usuarioRedRepository.findByUsuarioIdUsuarioAndRedCodRed(usuario.getIdUsuario(), red.getCodRed());
                usuarioRed.setFechaAsignacion(LocalDateTime.now(ZoneId.of("America/Lima")));
                usuarioRed.setHabilitado(true);
                usuarioRed.setUsuarioModificacion(authService.getIdUserSession());
            } else {
                UsuarioRedId usuarioRedId = new UsuarioRedId(usuario.getIdUsuario(), red.getCodRed());
                UsuarioRed usuarioRed = UsuarioRed.builder()
                        .id(usuarioRedId)
                        .usuario(usuario)
                        .red(red)
                        .habilitado(true)
                        .fechaAsignacion(LocalDateTime.now(ZoneId.of("America/Lima")))
                        .usuarioCreacion(authService.getIdUserSession())
                        .esActivo(true)
                        .build();
                usuarioRedRepository.save(usuarioRed);
            }
        });
    }

    @Override
    @Transactional
    public void habilitarUsuario(long idUsuario, boolean habilitado) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ValidationException("El usuario no se encuentra registrado"));

        usuarioRedRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .forEach(usuarioRed -> {
                    usuarioRed.setHabilitado(habilitado);
                    usuarioRed.setUsuarioModificacion(authService.getIdUserSession());
                    usuarioRedRepository.save(usuarioRed);
                });
    }

    @Override
    @Transactional
    public void habilitarUsuarioRed(long idUsuario, String codRed, boolean habilitado) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ValidationException("El usuario no se encuentra registrado"));

        RedPersonal red = redPersonalRepository.findById(codRed)
                .orElseThrow(() -> new ValidationException("La red no se encuentra registrada"));

        UsuarioRed usuarioRed = usuarioRedRepository.findByUsuarioIdUsuarioAndRedCodRed(usuario.getIdUsuario(), red.getCodRed());
        if (!usuarioRed.isHabilitado()) {
            usuarioRed.setFechaAsignacion(LocalDateTime.now(ZoneId.of("America/Lima")));
        }
        usuarioRed.setUsuarioModificacion(authService.getIdUserSession());
        usuarioRed.setHabilitado(habilitado);
        usuarioRedRepository.save(usuarioRed);
    }

    @Override
    @Transactional
    public void eliminarUsuarioRed(long idUsuario, String codRed) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ValidationException("El usuario no se encuentra registrado"));

        RedPersonal red = redPersonalRepository.findById(codRed)
                .orElseThrow(() -> new ValidationException("La red no se encuentra registrada"));

        UsuarioRed usuarioRed = usuarioRedRepository.findByUsuarioIdUsuarioAndRedCodRed(usuario.getIdUsuario(), red.getCodRed());
        usuarioRed.setEsActivo(false);
        usuarioRed.setUsuarioModificacion(authService.getIdUserSession());
        usuarioRedRepository.save(usuarioRed);

        List<String> redesAsignadas = publicacionRepository.findRedesAsignadasUsuario(authService.getIdUserSession());
        if (redesAsignadas.isEmpty()) {
            if (usuario.getIdRol() == RoleType.ADMIN_SEDE) {
                usuario.setIdRol(RoleType.TRABAJADOR);
            } else {
                usuario.setIdRolAdicional(null);
            }
            usuarioRepository.save(usuario);
        }
    }

}
