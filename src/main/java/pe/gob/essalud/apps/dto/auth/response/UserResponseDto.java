package pe.gob.essalud.apps.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@AllArgsConstructor
public class UserResponseDto {

    String username;
    Collection<GrantedAuthority> authorities;

}
