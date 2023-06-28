package pe.gob.essalud.apps.service.impl;

import pe.gob.essalud.apps.common.constants.EstadoUsuario;
import pe.gob.essalud.apps.common.constants.ValidationMsg;
import pe.gob.essalud.apps.model.miessalud.Usuario;
import pe.gob.essalud.apps.repository.miessalud.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Qualifier("app.users")
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final String ROL_PREFIX = "ROLE_";

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) {
        Usuario usuarioModel = usuarioRepository.findByNumeroDocumentoAndIdEstadoUsuario(username, EstadoUsuario.ACTIVADO)
                .orElseThrow(()-> new UsernameNotFoundException(ValidationMsg.USUARIO_NO_ENCONTRADO));

        return this.userBuilder(username, usuarioModel.getPassword(), usuarioModel.getIdRol());
    }

    private User userBuilder(String username, String password, int... roles) {
        List<GrantedAuthority> authorities = Arrays.stream(roles)
                .mapToObj(x -> new SimpleGrantedAuthority(ROL_PREFIX + x))
                .collect(Collectors.toList());

        return new User(username, password, true, true, true,
                true, authorities);
    }
}