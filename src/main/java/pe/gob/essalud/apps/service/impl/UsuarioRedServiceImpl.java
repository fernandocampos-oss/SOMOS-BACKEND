package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
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
import pe.gob.essalud.apps.repository.miessalud.RedPersonalRepository;
import pe.gob.essalud.apps.repository.miessalud.UsuarioRedRepository;
import pe.gob.essalud.apps.repository.miessalud.UsuarioRepository;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.UsuarioRedService;

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

    private final AuthService authService;

    @Override
    public List<UsuarioNombresResponse> listarAministradoresRed() {
        return usuarioRepository.findByIdRol(RoleType.ADMIN_SEDE).stream()
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
        var usuarios = usuarioRedList.stream().map(UsuarioRed::getUsuario).collect(Collectors.toList());

        Set<Usuario> uniqueUsers = new HashSet<>(usuarios);
        usuarios.clear();
        usuarios.addAll(uniqueUsers);

        return usuarios.stream()
                .map(u -> {
                    var response = new UsuarioRedResponse();
                    String nombres = Optional.ofNullable(u.getNombres()).orElse("");
                    String apellidos = Optional.ofNullable(u.getApellidos()).orElse("");
                    var usuarioNombre = UsuarioNombresResponse.builder()
                            .idUsuario(u.getIdUsuario())
                            .nombresCompletos(nombres + " " + apellidos)
                            .build();
                    response.setUsuario(usuarioNombre);
                    response.setRedes(new ArrayList<>());
                    response.setHabilitado(false);
                    usuarioRedList.forEach(ur -> {
                        if (ur.getUsuario().getIdUsuario() == u.getIdUsuario()) {
                            var redResponse = new RedResponse();
                            redResponse.setCodRed(ur.getRed().getCodRed());
                            redResponse.setDescripcion(ur.getRed().getDescripcion());
                            redResponse.setHabilitado(ur.isHabilitado());
                            redResponse.setFechaAsignacion(ur.getFechaCreacion());
                            response.getRedes().add(redResponse);
                            if (ur.isHabilitado()) {
                                response.setHabilitado(true);
                            }
                        }
                    });
                    var fechas = response.getRedes().stream().map(RedResponse::getFechaAsignacion).collect(Collectors.toList());
                    response.setFechaAsignacion(Collections.max(fechas));
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void asignarRedesUsuario(UsuarioRedRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new ValidationException("El usuario no se encuentra registrado"));

        asignarRedes(request, usuario);
    }

    @Override
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
            UsuarioRedId usuarioRedId = new UsuarioRedId(usuario.getIdUsuario(), red.getCodRed());
            UsuarioRed usuarioRed = UsuarioRed.builder()
                    .id(usuarioRedId)
                    .usuario(usuario)
                    .red(red)
                    .habilitado(true)
                    .usuarioCreacion(authService.getIdUserSession())
                    .esActivo(true)
                    .build();
            usuarioRedRepository.save(usuarioRed);
        });
    }

    @Override
    public void habilitarUsuario(long idUsuario, boolean habilitado) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ValidationException("El usuario no se encuentra registrado"));

        usuarioRedRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .forEach(usuarioRed -> {
                    usuarioRed.setHabilitado(habilitado);
                    usuarioRedRepository.save(usuarioRed);
                });
    }
}
