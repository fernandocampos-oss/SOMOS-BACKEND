package pe.gob.essalud.apps.service.impl;

import org.springframework.scheduling.annotation.Async;
import pe.gob.essalud.apps.base.BaseService;
import pe.gob.essalud.apps.client.EmailServiceClient;
import pe.gob.essalud.apps.client.PersonalSapUtilServiceClient;
import pe.gob.essalud.apps.dto.emailservice.ActivarCuentaRequestDto;
import pe.gob.essalud.apps.dto.emailservice.RecuperarClaveWebRequestDto;
import pe.gob.essalud.apps.dto.personalsaputilservice.PersonaSAP;
import pe.gob.essalud.apps.common.constants.Constantes;
import pe.gob.essalud.apps.common.constants.EstadoUsuario;
import pe.gob.essalud.apps.common.constants.RoleType;
import pe.gob.essalud.apps.common.util.StringUtil;
import pe.gob.essalud.apps.dto.auth.UserSessionDto;
import pe.gob.essalud.apps.dto.auth.request.AuthUsuarioRegisterRequestDto;
import pe.gob.essalud.apps.dto.auth.request.CambiarClaveRequestDto;
import pe.gob.essalud.apps.dto.auth.request.GenerarTokenRecuperarClaveRequestDto;
import pe.gob.essalud.apps.dto.auth.request.TokenActivacionRequestDto;
import pe.gob.essalud.apps.dto.auth.response.AuthUsuarioRegisterResponse;
import pe.gob.essalud.apps.dto.auth.response.GenerarTokenRecuperarClaveResponseDto;
import pe.gob.essalud.apps.exceptions.ValidationException;
import pe.gob.essalud.apps.model.miessalud.TokenActivacion;
import pe.gob.essalud.apps.model.miessalud.Usuario;
import pe.gob.essalud.apps.repository.miessalud.TokenActivacionRepository;
import pe.gob.essalud.apps.repository.miessalud.UsuarioRepository;
import pe.gob.essalud.apps.repository.miessalud.sqlmap.AuthMyRepository;
import pe.gob.essalud.apps.service.AuthService;
import pe.gob.essalud.apps.validators.TokenActivacionValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl extends BaseService implements AuthService {

    private static final int TOKEN_SIZE = 4;
    private static final int EXPIRATION_TIME_TOKEN_ACTIVATION_IN_MINUTES = 5;
    private static final int EXPIRATION_TIME_TOKEN_RECOVERY_IN_MINUTES = 30;

    private final UsuarioRepository usuarioRepository;
    private final TokenActivacionRepository tokenActivacionRepository;
    private final TokenActivacionValidator tokenActivacionValidator;
    private final AuthMyRepository authMyRepository;
    private final ModelMapper modelMapper;
    private final PersonalSapUtilServiceClient _personalSapUtilServiceClient;
    private final EmailServiceClient _emailServiceClient;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserSessionDto getUserSession() {
        Object user = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        if (!(user instanceof UserSessionDto))
            throw new ValidationException("No existe usuario en sessión");
        else {
            return (UserSessionDto) user;
        }
    }

    @Override
    public int getIdUserSession() {
        return getUserSession().getId();
    }

    @Override
    public boolean hasRole(int idRole) {
        return getUserSession().getIdRol() == idRole;
    }

    @Override
    public int getIdSedeSession() {
        return getUserSession().getIdSede();
    }

    @Override
    public Integer getIdZonaControlSession() {
        return getUserSession().getIdZonaControl();
    }

    @Override
    public UserSessionDto findByUsername(String username) {
        return authMyRepository.findByUsername(username);
    }

    @Async
    protected void _sendMailActivarCuenta(String correo, String token) {
        ActivarCuentaRequestDto requestActivarCuenta = new ActivarCuentaRequestDto();
        requestActivarCuenta.setEmail(correo);
        requestActivarCuenta.setToken(token);
        _emailServiceClient.activarCuenta(requestActivarCuenta);
    }

    @Async
    protected void _sendMailRecuperarClave(String correo, String url) {
        RecuperarClaveWebRequestDto requestRecuperarClave = new RecuperarClaveWebRequestDto();
        requestRecuperarClave.setEmail(correo);
        requestRecuperarClave.setUrl(url);
        _emailServiceClient.recuperarClave(requestRecuperarClave);
    }

    @Override
    @Transactional
    public AuthUsuarioRegisterResponse save(AuthUsuarioRegisterRequestDto model) {
        Usuario usuarioModel = usuarioRepository
                .findByNumeroDocumentoAndIdEstadoUsuarioAndEsActivo(model.getNumeroDocumento(),EstadoUsuario.ACTIVADO,true)
                .orElse(null);

        boolean alreadyRegistered = usuarioModel != null;

        if (alreadyRegistered)
            throw new ValidationException(
                    "Ya existe un usuario con el número de documento o código de planilla ingresado");

        PersonaSAP personaSAP = _personalSapUtilServiceClient.getByNumDocAndFecNac(
                model.getNumeroDocumento(),
                model.getFechaNacimiento().toString());

        if (personaSAP == null)
            throw new ValidationException("Datos incorrectos");

        boolean codPlanillaValid = personaSAP.getCodPlanilla().equals(model.getCodigoPlanilla());
        if (!codPlanillaValid)
            throw new ValidationException("Datos incorrectos");

        usuarioModel = modelMapper.map(model, Usuario.class);

        String[] nombresArray = personaSAP.getNombres().split(",");
        usuarioModel.setNombres(nombresArray[1]);
        usuarioModel.setApellidos(nombresArray[0]);
        usuarioModel.setSexo(personaSAP.getSexot());
        usuarioModel.setRegimen(personaSAP.getRegimen());
        usuarioModel.setCargo(personaSAP.getCargo());
        usuarioModel.setFechaIngreso(personaSAP.getFechaIngreso());
        usuarioModel.setCodigoRed(personaSAP.getWerks());
        usuarioModel.setCodigoUnidad(personaSAP.getOrgeh());
        usuarioModel.setEsActivo(true);
        usuarioModel.setIdRol(RoleType.TRABAJADOR);
        usuarioModel.setIdEstadoUsuario(EstadoUsuario.PENDIENTE_ACTIVACION);
        usuarioModel.setPassword(passwordEncoder.encode(model.getPassword()));
        usuarioModel.setIdSede(0);
        usuarioModel.setIdZonaControl(0);
        usuarioRepository.save(usuarioModel);

        long idUsuario = usuarioModel.getIdUsuario();
        String token = generarToken(idUsuario, model.getCorreo());
        String correo = usuarioModel.getCorreo();
        _sendMailActivarCuenta(correo, token);

        return AuthUsuarioRegisterResponse.builder()
                .idUsuario(idUsuario)
                .token(token)
                .build();
    }

    @Override
    @Transactional
    public GenerarTokenRecuperarClaveResponseDto generarTokenRecuperarClave(GenerarTokenRecuperarClaveRequestDto request) {
        String username = request.getNumeroDocumento();
        Usuario usuarioModel = usuarioRepository.findByNumeroDocumentoAndIdEstadoUsuarioAndEsActivo(username, EstadoUsuario.ACTIVADO, true)
                .orElseThrow(() -> new ValidationException("El usuario no se encuentra registrado"));

        String token = UUID.randomUUID().toString();
        LocalDateTime fechaActual = LocalDateTime.now();
        LocalDateTime fechaExpiracion = fechaActual.plusMinutes(EXPIRATION_TIME_TOKEN_RECOVERY_IN_MINUTES);

        TokenActivacion tokenRegistroModel = TokenActivacion.builder()
                .fechaExpiracion(fechaExpiracion)
                .esConfirmado(false)
                .correo(usuarioModel.getCorreo())
                .idUsuario(usuarioModel.getIdUsuario())
                .token(token)
                .build();

        tokenActivacionRepository.save(tokenRegistroModel);
        String url = getProperty(Constantes.URL_REDIRECT_RECUPERAR_CLAVE);

        url = UriComponentsBuilder.fromUriString(url).build().encode().toUriString();
        url += "?token=" + token;
        url += "&username=" + username;

        String correo = usuarioModel.getCorreo();
        _sendMailRecuperarClave(correo, url);

        return new GenerarTokenRecuperarClaveResponseDto(correo);
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

    @Override
    @Transactional
    public void activarToken(TokenActivacionRequestDto request, Boolean validateUser) {
        tokenActivacionValidator.validateActivation(request, validateUser);

        // Actualizar confirmación token
        tokenActivacionRepository
                .findTopByIdUsuarioOrderByFechaCreacionDesc(request.getIdUsuario())
                .map(tokenActivacionModel -> {
                    tokenActivacionModel.setEsConfirmado(true);
                    tokenActivacionRepository.save(tokenActivacionModel);
                    return usuarioRepository.findById(request.getIdUsuario()).orElse(null);
                })
                .ifPresent(x -> {
                    x.setIdEstadoUsuario(EstadoUsuario.ACTIVADO);
                    usuarioRepository.save(x);
                });
    }

    @Override
    @Transactional
    public void cambiarClave(CambiarClaveRequestDto request) {

        Usuario usuarioModel = usuarioRepository.findByNumeroDocumentoAndIdEstadoUsuarioAndEsActivo(request.getUsername(), EstadoUsuario.ACTIVADO, true)
                .orElseThrow(() -> new ValidationException("Usuario no está registrado."));

        TokenActivacion tokenModel = tokenActivacionRepository
                .findTopByIdUsuarioAndTokenOrderByFechaCreacionDesc(usuarioModel.getIdUsuario(), request.getToken())
                .orElseThrow(() -> new ValidationException("El token no es válido"));

        tokenActivacionValidator.validateTokenAndExpiration(tokenModel, request.getToken());

        usuarioModel.setPassword(passwordEncoder.encode(request.getNuevaClave()));
        usuarioRepository.save(usuarioModel);

        tokenModel.setEsConfirmado(true);
        tokenActivacionRepository.save(tokenModel);
    }

}
