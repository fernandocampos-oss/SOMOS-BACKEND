package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.base.BaseService;
import pe.gob.essalud.apps.client.EmailServiceClient;
import pe.gob.essalud.apps.client.PersonalSapUtilServiceClient;
import pe.gob.essalud.apps.common.constants.EstadoUsuario;
import pe.gob.essalud.apps.common.constants.RoleType;
import pe.gob.essalud.apps.common.util.StringUtil;
import pe.gob.essalud.apps.common.util.UploadUtil;
import pe.gob.essalud.apps.dto.emailservice.ActivarCuentaRequestDto;
import pe.gob.essalud.apps.dto.personalsaputilservice.PersonaSAP;
import pe.gob.essalud.apps.dto.usuario.request.UsuarioActualizarDatosRequestDto;
import pe.gob.essalud.apps.dto.usuario.request.UsuarioCambiarClaveRequestDto;
import pe.gob.essalud.apps.dto.usuario.request.UsuarioCambiarCorreoRequestDto;
import pe.gob.essalud.apps.dto.usuario.request.UsuarioRegisterUpdateRequestDto;
import pe.gob.essalud.apps.dto.usuario.response.UsuarioNombresResponse;
import pe.gob.essalud.apps.dto.usuario.response.UsuarioResponseDto;
import pe.gob.essalud.apps.exceptions.ForbiddenException;
import pe.gob.essalud.apps.exceptions.NotFoundException;
import pe.gob.essalud.apps.exceptions.ValidationException;
import pe.gob.essalud.apps.model.miessalud.TokenActivacion;
import pe.gob.essalud.apps.model.miessalud.Usuario;
import pe.gob.essalud.apps.repository.miessalud.TokenActivacionRepository;
import pe.gob.essalud.apps.repository.miessalud.UsuarioRepository;
import pe.gob.essalud.apps.repository.miessalud.sqlmap.UsuarioMyRepository;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.service.UsuarioService;
import pe.gob.essalud.apps.validators.TokenActivacionValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl extends BaseService implements UsuarioService {

    private static final String RUTA_IMAGENES_PERFILES = "/imagenes/perfiles/";
    private static final String RUTA_IMAGENES_FIRMAS = "/imagenes/firmas/";
    private static final String FORMATO_IMAGEN = ".png";
    private static final int TOKEN_SIZE = 4;
    private static final int EXPIRATION_TIME_TOKEN_ACTIVATION_IN_MINUTES = 5;

    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMyRepository usuarioMyRepository;
    private final TokenActivacionRepository tokenActivacionRepository;
    private final TokenActivacionValidator tokenActivacionValidator;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final PersonalSapUtilServiceClient _personalSapUtilServiceClient;
    private final EmailServiceClient _emailServiceClient;

    @Value("${upload-path}")
    private String uploadPath;

    @Override
    public List<UsuarioResponseDto> search() {
        List<Usuario> users = getMyUsers();
        return users.stream()
                .map(x -> modelMapper.map(x, UsuarioResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioResponseDto get(long id) {
        UsuarioResponseDto usuarioResponseDto = usuarioMyRepository.findById(id);
        usuarioResponseDto.setImagenPerfilBase64(UploadUtil.getFileBase64(usuarioResponseDto.getRutaImagenPerfil()));
        usuarioResponseDto.setImagenFirmaBase64(UploadUtil.getFileBase64(usuarioResponseDto.getRutaImagenFirma()));
        return usuarioResponseDto;
    }

    @Override
    public UsuarioResponseDto find(long id) {
        return usuarioMyRepository.findById(id);
    }

    @Override
    public void update(long id, UsuarioRegisterUpdateRequestDto model) {
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
            validateRed(usuarioModel.getCodigoRed());
        usuarioRepository.deleteById(id);
    }

    private void validateRed(String codRed) {
        boolean userMatchesMyRed = Objects.equals(authService.getCodRedSession(), codRed);
        if (!userMatchesMyRed)
            throw new ForbiddenException();
    }

    @Override
    public long save(UsuarioRegisterUpdateRequestDto model) {
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
        if(authService.getIdUserSession() != id) {
            throw new ValidationException("No puede actualizar la clave de un usuario diferente al de la sesión");
        }

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ValidationException("El usuario no se encuentra registrado"));

        if (!passwordEncoder.matches(request.getActualClave(), usuario.getPassword())) {
            throw new ValidationException("La contraseña actual no coincide con el valor ingresado");
        }

        usuario.setPassword(passwordEncoder.encode(request.getNuevaClave()));
        usuarioRepository.save(usuario);
    }

    @Override
    public void updateDatosSAP(long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        PersonaSAP personaSAP = _personalSapUtilServiceClient.getByNumDocAndFecNac(
                usuario.getNumeroDocumento(),
                usuario.getFechaNacimiento().toString());

        if (personaSAP != null) {
            String[] nombresArray = personaSAP.getNombres().split(",");
            usuario.setNombres(nombresArray[1]);
            usuario.setApellidos(nombresArray[0]);
            usuario.setSexo(personaSAP.getSexot());
            usuario.setRegimen(personaSAP.getRegimen());
            usuario.setCargo(personaSAP.getCargo());
            usuario.setFechaIngreso(personaSAP.getFechaIngreso());
            usuario.setCodigoRed(personaSAP.getWerks());
            usuario.setCodigoUnidad(personaSAP.getOrgeh());
            usuarioRepository.save(usuario);
        }
    }

    @Override
    public void actualizarDatos(long id, UsuarioActualizarDatosRequestDto request) {
        if(authService.getIdUserSession() != id) {
            throw new ValidationException("No puede actualizar los datos de un usuario diferente al de la sesión");
        }

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ValidationException("El usuario no se encuentra registrado"));

        usuario.setNumeroCelular(request.getNumeroCelular());
        if (!usuario.getCorreo().equalsIgnoreCase(request.getCorreo())) {
            String token = generarToken(id, request.getCorreo());
            _sendMailActivarCorreo(request.getCorreo(), token);
        }

        String rutaImagenPerfil = uploadPath + RUTA_IMAGENES_PERFILES + id + FORMATO_IMAGEN;
        rutaImagenPerfil = UploadUtil.saveFileBase64(rutaImagenPerfil, request.getImagenPerfilBase64());
        usuario.setRutaImagenPerfil(rutaImagenPerfil);

        String rutaImagenFirma = uploadPath + RUTA_IMAGENES_FIRMAS + id + FORMATO_IMAGEN;
        rutaImagenFirma = UploadUtil.saveFileBase64(rutaImagenFirma, request.getImagenFirmaBase64());
        usuario.setRutaImagenFirma(rutaImagenFirma);

        usuarioRepository.save(usuario);
    }

    @Override
    public void cambiarCorreo(long id, UsuarioCambiarCorreoRequestDto request) {
        if(authService.getIdUserSession() != id) {
            throw new ValidationException("No puede actualizar los datos de un usuario diferente al de la sesión");
        }

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ValidationException("El usuario no se encuentra registrado"));

        TokenActivacion tokenModel = tokenActivacionRepository
                .findTopByIdUsuarioAndTokenOrderByFechaCreacionDesc(id, request.getToken())
                .orElseThrow(() -> new ValidationException("El token no es válido"));

        tokenActivacionValidator.validateTokenAndExpiration(tokenModel, request.getToken());

        usuario.setCorreo(request.getNuevoCorreo());
        usuarioRepository.save(usuario);

        tokenModel.setEsConfirmado(true);
        tokenActivacionRepository.save(tokenModel);
    }

    @Async
    protected void _sendMailActivarCorreo(String correo, String token) {
        ActivarCuentaRequestDto requestActivarCuenta = new ActivarCuentaRequestDto();
        requestActivarCuenta.setEmail(correo);
        requestActivarCuenta.setToken(token);
        _emailServiceClient.activarCuenta(requestActivarCuenta);
    }

    private List<Usuario> getMyUsers() {
        return authService.hasRole(RoleType.ADMIN_CENTRAL)
                ? usuarioRepository.findAllByIdEstadoUsuarioOrderByNombres(EstadoUsuario.ACTIVADO)
                : usuarioRepository.findAllByCodigoRed(authService.getCodRedSession());
    }

    private String generarToken(long idUsuario, String correo) {
        String token = StringUtil.getRandomNumber(TOKEN_SIZE);
        LocalDateTime fechaExpiracion = LocalDateTime
                .now()
                .plusMinutes(EXPIRATION_TIME_TOKEN_ACTIVATION_IN_MINUTES);

        TokenActivacion tokenRegistroModel = TokenActivacion.builder()
                .fechaExpiracion(fechaExpiracion)
                .token(token)
                .correo(correo)
                .idUsuario(idUsuario)
                .build();

        tokenActivacionRepository.save(tokenRegistroModel);
        return token;
    }

}
