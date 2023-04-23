package com.marcas.service;

import com.marcas.base.BaseService;
import com.marcas.client.personalsap.PersonalSapClient;
import com.marcas.client.personalsap.model.PersonaSAP;
import com.marcas.common.constants.Constantes;
import com.marcas.common.constants.EstadoUsuario;
import com.marcas.common.constants.RoleType;
import com.marcas.common.email.EmailContentBuilder;
import com.marcas.common.email.EmailSender;
import com.marcas.common.util.StringUtil;
import com.marcas.dto.auth.UserSessionDto;
import com.marcas.dto.auth.request.AuthUsuarioRegisterRequestDto;
import com.marcas.dto.auth.request.CambiarClaveRequestDto;
import com.marcas.dto.auth.request.GenerarTokenRecuperarClaveRequestDto;
import com.marcas.dto.auth.request.TokenActivacionRequestDto;
import com.marcas.dto.auth.response.AuthUsuarioRegisterResponse;
import com.marcas.dto.auth.response.GenerarTokenRecuperarClaveResponseDto;
import com.marcas.exceptions.ValidationException;
import com.marcas.model.marcaciones.TokenActivacion;
import com.marcas.model.marcaciones.Usuario;
import com.marcas.repository.marcaciones.TokenActivacionRepository;
import com.marcas.repository.marcaciones.UsuarioRepository;
import com.marcas.repository.marcaciones.sqlmap.AuthMyRepository;
import com.marcas.validators.TokenActivacionValidator;
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
    private final EmailSender emailSender;
    private final EmailContentBuilder mailContentBuilder;
    private final PersonalSapClient personalSapClient;

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

    @Override
    @Transactional
    public AuthUsuarioRegisterResponse save(AuthUsuarioRegisterRequestDto model) {
        Usuario usuarioModel = usuarioRepository
                .findByNumeroDocumento(
                        model.getNumeroDocumento())
                .orElse(null);

        boolean alreadyRegistered = usuarioModel != null && usuarioModel
                .getIdEstadoUsuario()
                .equals(EstadoUsuario.ACTIVADO);

        if (alreadyRegistered)
            throw new ValidationException(
                    "Ya existe un usuario con el número de documento o código de planilla ingresado");

        PersonaSAP personaSAP = personalSapClient.getPorNumeroDocumentoAndFechaNacimiento(
                model.getNumeroDocumento(),
                model.getFechaNacimiento());

        if (personaSAP == null)
            throw new ValidationException("Datos incorrectos");

        boolean codPlanillaValid = personaSAP.getCodPlanilla().equals(model.getCodigoPlanilla());
        if (!codPlanillaValid)
            throw new ValidationException("Datos incorrectos");

        if (usuarioModel != null)
            modelMapper.map(model, usuarioModel);
        else
            usuarioModel = modelMapper.map(model, Usuario.class);

        String[] nombresArray = personaSAP.getNombres().split(",");
        usuarioModel.setNombres(nombresArray[1]);
        usuarioModel.setApellidos(nombresArray[0]);
        usuarioModel.setEsActivo(true);
        usuarioModel.setIdRol(RoleType.TRABAJADOR);
        usuarioModel.setIdEstadoUsuario(EstadoUsuario.PENDIENTE_ACTIVACION);
        usuarioModel.setPassword(passwordEncoder.encode(model.getPassword()));
        usuarioModel.setIdSede(0);
        usuarioModel.setIdZonaControl(0);
        usuarioRepository.save(usuarioModel);

        long idUsuario = usuarioModel.getIdUsuario();
        String token = generarToken(idUsuario, model.getCorreo());

        return AuthUsuarioRegisterResponse.builder()
                .idUsuario(idUsuario)
                .token(token)
                .build();
    }

    @Override
    @Transactional
    public GenerarTokenRecuperarClaveResponseDto generarTokenRecuperarClave(GenerarTokenRecuperarClaveRequestDto request) {
        String username = request.getNumeroDocumento();
        Usuario usuarioModel = usuarioRepository.findByNumeroDocumento(username)
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
        String message = mailContentBuilder.resetPassword(url);
        String correo = usuarioModel.getCorreo();
        emailSender.send(
                correo,
                "Restablecer Contraseña",
                message
        );
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

        Usuario usuarioModel = usuarioRepository.findByNumeroDocumento(request.getUsername())
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
