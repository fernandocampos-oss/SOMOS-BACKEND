package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pe.gob.essalud.apps.client.PlazaSapServiceClient;
import pe.gob.essalud.apps.dto.personal.response.PersonalLogeoResponseDto;
import pe.gob.essalud.apps.dto.personal.response.PersonalTokenResponseDto;
import pe.gob.essalud.apps.dto.plaza.response.PlazaResponseDto;
import pe.gob.essalud.apps.model.miessalud.RedPersonal;
import pe.gob.essalud.apps.model.miessalud.UnidadOrganizativa;
import pe.gob.essalud.apps.model.miessalud.Usuario;
import pe.gob.essalud.apps.repository.miessalud.RedPersonalRepository;
import pe.gob.essalud.apps.repository.miessalud.UnidadOrganizativaRepository;
import pe.gob.essalud.apps.repository.miessalud.UsuarioRepository;
import pe.gob.essalud.apps.service.JwtService;
import pe.gob.essalud.apps.service.PersonalTokenService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonalTokenServiceImpl implements PersonalTokenService {

    private final UsuarioRepository usuarioRepository;
    private final RedPersonalRepository redPersonalRepository;
    private final UnidadOrganizativaRepository unidadOrganizativaRepository;
    private final PlazaSapServiceClient plazaSapServiceClient;
    private final JwtService jwtService;
    private final HttpServletRequest request;
    
    @Value("${plataforma-integral.url:http://localhost:4201/verificar-sso}")
    private String plataformaIntegralUrl;
    
    // Almacén temporal de tokens (en producción usar Redis)
    private static final Map<String, TokenData> tokenStore = new ConcurrentHashMap<>();
    private static final long TOKEN_EXPIRATION_SECONDS = 60; // 1 minuto
    
    @Override
    public PersonalTokenResponseDto generarToken() {
        // Obtener id del usuario del JWT
        String authorization = request.getHeader("Authorization");
        int userId = jwtService.id(authorization);
        
        Usuario usuario = usuarioRepository.findById((long) userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Obtener descripción de la Red (Dependencia)
        String dependencia = null;
        if (usuario.getCodigoRed() != null) {
            RedPersonal red = redPersonalRepository.findByCodRed(usuario.getCodigoRed());
            if (red != null) {
                dependencia = red.getDescripcion();
            }
        }
        
        // Obtener descripción de la Unidad (Lugar donde labora)
        String lugarLabora = null;
        if (usuario.getCodigoUnidad() != null) {
            UnidadOrganizativa unidad = unidadOrganizativaRepository.findFirstByCodUnidad(usuario.getCodigoUnidad());
            if (unidad != null) {
                lugarLabora = unidad.getDescripcion();
            }
        }
        
        // Intentar obtener plaza y nivel desde SAP
        String numeroPlaza = null;
        String nivelCargo = null;
        try {
            PlazaResponseDto plazaData = null;
            
            // Intento 1: Buscar por código de planilla como número de plaza
            if (usuario.getCodigoPlanilla() != null && !usuario.getCodigoPlanilla().isEmpty()) {
                try {
                    plazaData = plazaSapServiceClient.getPlaza(usuario.getCodigoPlanilla(), "");
                    log.info("Búsqueda SAP por código planilla {}: {}", usuario.getCodigoPlanilla(), plazaData != null ? "encontrado" : "no encontrado");
                } catch (Exception e) {
                    log.debug("No se encontró por código planilla: {}", e.getMessage());
                }
            }
            
            // Intento 2: Si no encontró, buscar por nombre completo
            if (plazaData == null || plazaData.getPlaza() == null) {
                String nombreCompleto = (usuario.getApellidos() != null ? usuario.getApellidos() : "") + " " + 
                                       (usuario.getNombres() != null ? usuario.getNombres() : "");
                try {
                    plazaData = plazaSapServiceClient.getPlaza("", nombreCompleto.trim());
                    log.info("Búsqueda SAP por nombre {}: {}", nombreCompleto.trim(), plazaData != null ? "encontrado" : "no encontrado");
                } catch (Exception e) {
                    log.debug("No se encontró por nombre: {}", e.getMessage());
                }
            }
            
            if (plazaData != null) {
                numeroPlaza = plazaData.getPlaza();
                nivelCargo = plazaData.getNivel();
            }
        } catch (Exception e) {
            log.warn("No se pudo obtener datos de plaza/nivel desde SAP para usuario {}: {}", 
                    usuario.getNumeroDocumento(), e.getMessage());
        }
        
        // Formatear fecha de nacimiento
        String fechaNacimiento = null;
        if (usuario.getFechaNacimiento() != null) {
            fechaNacimiento = usuario.getFechaNacimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        
        // Generar token único
        String token = UUID.randomUUID().toString().replace("-", "");
        
        // Guardar token con datos del usuario
        TokenData tokenData = new TokenData(
            usuario.getIdUsuario(),
            usuario.getNombres(),
            usuario.getApellidos(),
            usuario.getNumeroDocumento(),
            usuario.getCorreo(),
            fechaNacimiento,
            usuario.getNumeroCelular(),
            usuario.getSexo(),
            usuario.getCodigoPlanilla(),
            usuario.getCargo(),
            usuario.getRegimen(),
            usuario.getFechaIngreso(),
            dependencia,
            lugarLabora,
            numeroPlaza,
            nivelCargo,
            LocalDateTime.now().plusSeconds(TOKEN_EXPIRATION_SECONDS)
        );
        tokenStore.put(token, tokenData);
        
        // Limpiar tokens expirados
        limpiarTokensExpirados();
        
        // Devolver URL base sin token
        return new PersonalTokenResponseDto(token, plataformaIntegralUrl, TOKEN_EXPIRATION_SECONDS);
    }
    
    @Override
    public PersonalLogeoResponseDto validarToken(String token) {
        TokenData tokenData = tokenStore.get(token);
        
        if (tokenData == null) {
            return PersonalLogeoResponseDto.builder()
                    .success(false)
                    .mensaje("Token no válido o no existe")
                    .build();
        }
        
        if (LocalDateTime.now().isAfter(tokenData.expiration)) {
            tokenStore.remove(token);
            return PersonalLogeoResponseDto.builder()
                    .success(false)
                    .mensaje("Token expirado")
                    .build();
        }
        
        // Token válido - eliminar para que solo se use una vez
        tokenStore.remove(token);
        
        return PersonalLogeoResponseDto.builder()
                .success(true)
                .mensaje("Autenticación exitosa")
                .nombres(tokenData.nombres)
                .apellidos(tokenData.apellidos)
                .dni(tokenData.dni)
                .correo(tokenData.correo)
                .fechaNacimiento(tokenData.fechaNacimiento)
                .numeroCelular(tokenData.numeroCelular)
                .sexo(tokenData.sexo)
                .codigoPlanilla(tokenData.codigoPlanilla)
                .cargo(tokenData.cargo)
                .regimen(tokenData.regimen)
                .fechaIngreso(tokenData.fechaIngreso)
                .dependencia(tokenData.dependencia)
                .lugarLabora(tokenData.lugarLabora)
                .numeroPlaza(tokenData.numeroPlaza)
                .nivelCargo(tokenData.nivelCargo)
                .build();
    }
    
    private void limpiarTokensExpirados() {
        LocalDateTime now = LocalDateTime.now();
        tokenStore.entrySet().removeIf(entry -> now.isAfter(entry.getValue().expiration));
    }
    
    // Clase interna para almacenar datos del token
    private static class TokenData {
        final Long userId;
        final String nombres;
        final String apellidos;
        final String dni;
        final String correo;
        final String fechaNacimiento;
        final String numeroCelular;
        final String sexo;
        final String codigoPlanilla;
        final String cargo;
        final String regimen;
        final String fechaIngreso;
        final String dependencia;
        final String lugarLabora;
        final String numeroPlaza;
        final String nivelCargo;
        final LocalDateTime expiration;
        
        TokenData(Long userId, String nombres, String apellidos, String dni, 
                  String correo, String fechaNacimiento, String numeroCelular, String sexo,
                  String codigoPlanilla, String cargo, String regimen, String fechaIngreso,
                  String dependencia, String lugarLabora, String numeroPlaza, String nivelCargo,
                  LocalDateTime expiration) {
            this.userId = userId;
            this.nombres = nombres;
            this.apellidos = apellidos;
            this.dni = dni;
            this.correo = correo;
            this.fechaNacimiento = fechaNacimiento;
            this.numeroCelular = numeroCelular;
            this.sexo = sexo;
            this.codigoPlanilla = codigoPlanilla;
            this.cargo = cargo;
            this.regimen = regimen;
            this.fechaIngreso = fechaIngreso;
            this.dependencia = dependencia;
            this.lugarLabora = lugarLabora;
            this.numeroPlaza = numeroPlaza;
            this.nivelCargo = nivelCargo;
            this.expiration = expiration;
        }
    }
}
