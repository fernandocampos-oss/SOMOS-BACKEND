package pe.gob.essalud.apps.validators;

import pe.gob.essalud.apps.common.constants.EstadoUsuario;
import pe.gob.essalud.apps.dto.auth.request.TokenActivacionRequestDto;
import pe.gob.essalud.apps.exceptions.ValidationException;
import pe.gob.essalud.apps.model.miessalud.TokenActivacion;
import pe.gob.essalud.apps.model.miessalud.Usuario;
import pe.gob.essalud.apps.repository.miessalud.TokenActivacionRepository;
import pe.gob.essalud.apps.repository.miessalud.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TokenActivacionValidatorImpl implements TokenActivacionValidator {

    private final UsuarioRepository usuarioRepository;
    private final TokenActivacionRepository tokenRegistroRepository;
    private static final String DEFAULT_VALIDATION_MESSAGE = "El token no es válido";

    private void _validateUser(TokenActivacionRequestDto request) {
        Usuario model = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new ValidationException("El usuario no existe"));

        if (model.getIdEstadoUsuario().equals(EstadoUsuario.ACTIVADO))
            throw new ValidationException("El usuario se encuentra activado");

    }

    @Override
    public void validateActivation(TokenActivacionRequestDto request, boolean validateUser) {

        if (validateUser)
            _validateUser(request);
        // Validar existencia de código de activación
        TokenActivacion tokenRegistro = tokenRegistroRepository
                .findTopByIdUsuarioOrderByFechaCreacionDesc(request.getIdUsuario())
                .orElseThrow(() -> new ValidationException(DEFAULT_VALIDATION_MESSAGE));
        validateTokenAndExpiration(tokenRegistro, request.getToken());
    }

    @Override
    public void validateTokenAndExpiration(TokenActivacion model, String token) {
        boolean tokenNotMatch = !model.getToken().equals(token);
        if (tokenNotMatch)
            throw new ValidationException(DEFAULT_VALIDATION_MESSAGE);

        Boolean isConfirmed = model.getEsConfirmado();
        boolean tokenAlreadyActivated = isConfirmed != null && isConfirmed;

        if (tokenAlreadyActivated)
            throw new ValidationException(DEFAULT_VALIDATION_MESSAGE);

        // Validar expiración de código de activación
        boolean isExpired = LocalDateTime.now().isAfter(model.getFechaExpiracion());
        if (isExpired)
            throw new ValidationException("El código de activación ha expirado");
    }

}
