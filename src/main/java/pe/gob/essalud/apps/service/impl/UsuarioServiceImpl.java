package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.base.BaseService;
import pe.gob.essalud.apps.common.constants.EstadoUsuario;
import pe.gob.essalud.apps.common.constants.RoleType;
import pe.gob.essalud.apps.dto.usuario.request.UsuarioCambiarClaveRequestDto;
import pe.gob.essalud.apps.dto.usuario.request.UsuarioRegisterUpdateRequestDto;
import pe.gob.essalud.apps.dto.usuario.response.UsuarioNombresResponse;
import pe.gob.essalud.apps.dto.usuario.response.UsuarioResponseDto;
import pe.gob.essalud.apps.exceptions.ForbiddenException;
import pe.gob.essalud.apps.exceptions.NotFoundException;
import pe.gob.essalud.apps.exceptions.ValidationException;
import pe.gob.essalud.apps.model.miessalud.Usuario;
import pe.gob.essalud.apps.repository.miessalud.UsuarioRepository;
import pe.gob.essalud.apps.repository.miessalud.sqlmap.UsuarioMyRepository;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.UsuarioService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl extends BaseService implements UsuarioService {

    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMyRepository usuarioMyRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UsuarioResponseDto> search() {
        List<Usuario> users = getMyUsers();
        return users.stream()
                .map(x -> modelMapper.map(x, UsuarioResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioResponseDto get(long id) {
        return usuarioMyRepository.findById(id);
    }

    @Override
    public void update(long id, UsuarioRegisterUpdateRequestDto model) {
        validateRoleRegisterUpdateUser(model);
        boolean alreadyExists = usuarioRepository.existsByNumeroDocumentoOrCodigoPlanillaAndIdUsuarioNot(
                model.getNumeroDocumento(),
                model.getCodigoPlanilla(),
                id);
        if (alreadyExists)
            throw new ValidationException(
                    "Ya existe un usuario registro con el número de documento o código de planilla");
        Usuario usuarioModel = usuarioRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        modelMapper.map(model, usuarioModel);
        usuarioModel.setUsuarioModificacion(authService.getIdUserSession());
        usuarioRepository.save(usuarioModel);
    }

    @Override
    public void delete(long id) {
        Usuario usuarioModel = usuarioRepository.findById(id)
                .orElseThrow(() -> new ValidationException("El usuario no existe"));
        if (authService.hasRole(RoleType.ADMIN_SEDE))
            validateSede(usuarioModel.getIdSede());
        usuarioRepository.deleteById(id);
    }

    private void validateSede(int idSede) {
        boolean userMatchesMySede = authService.getIdSedeSession() == idSede;
        if (!userMatchesMySede)
            throw new ForbiddenException();
    }

    @Override
    public long save(UsuarioRegisterUpdateRequestDto model) {
        validateRoleRegisterUpdateUser(model);
        boolean alreadyExists = usuarioRepository.existsByNumeroDocumentoOrCodigoPlanilla(
                model.getNumeroDocumento(),
                model.getCodigoPlanilla());
        if (alreadyExists)
            throw new ValidationException(
                    "Ya existe un usuario registro con el número de documento o código de planilla");
        Usuario usuarioModel = modelMapper.map(model, Usuario.class);
        usuarioModel.setEsActivo(true);
        String password = passwordEncoder.encode(model.getNumeroDocumento());
        usuarioModel.setPassword(password);
        usuarioModel.setIdEstadoUsuario(EstadoUsuario.ACTIVADO);
        usuarioRepository.save(usuarioModel);
        return usuarioModel.getIdUsuario();
    }

    private void validateRoleRegisterUpdateUser(UsuarioRegisterUpdateRequestDto model) {
        if (authService.hasRole(RoleType.ADMIN_SEDE)) {
            if (model.getIdRol() == RoleType.ADMIN_CENTRAL)
                throw new ForbiddenException();
            model.setIdSede(authService.getIdSedeSession());
        }
    }

    @Override
    public List<UsuarioNombresResponse> getNombres(boolean mostrarTodos) {
        var users = mostrarTodos
                ? usuarioRepository.findAllByIdEstadoUsuarioOrderByNombres(EstadoUsuario.ACTIVADO)
                : getMyUsers();
        return users.stream().map(x -> {
            String nombres = Optional.ofNullable(x.getNombres()).orElse("");
            String apellidos = Optional.ofNullable(x.getApellidos()).orElse("");
            return UsuarioNombresResponse
                    .builder()
                    .idUsuario(x.getIdUsuario())
                    .nombresCompletos(nombres + " " + apellidos)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public void cambiarClave(long id, UsuarioCambiarClaveRequestDto request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ValidationException("El usuario no se encuentra registrado"));

        if (!passwordEncoder.matches(request.getActualClave(), usuario.getPassword())) {
            throw new ValidationException("La contraseña actual no coincide con el valor ingresado");
        }

        usuario.setPassword(passwordEncoder.encode(request.getNuevaClave()));
        usuarioRepository.save(usuario);
    }

    private List<Usuario> getMyUsers() {
        return authService.hasRole(RoleType.ADMIN_CENTRAL)
                ? usuarioRepository.findAllByIdEstadoUsuarioOrderByNombres(EstadoUsuario.ACTIVADO)
                : usuarioRepository.findAllByIdSede(authService.getIdSedeSession());
    }
}
