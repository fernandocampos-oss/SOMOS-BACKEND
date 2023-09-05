package pe.gob.essalud.apps.filters;

import com.auth0.jwt.exceptions.TokenExpiredException;
import pe.gob.essalud.apps.dto.auth.UserSessionDto;
import pe.gob.essalud.apps.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private static final String AUTHORIZATION = "Authorization";
    private static final String ROLE_PREFIX = "ROLE_";

    @Autowired
    private JwtService jwtService;

    public JwtAuthorizationFilter(AuthenticationManager authManager) {
        super(authManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        try {
            String authHeader = request.getHeader(AUTHORIZATION);
            if (jwtService.isBearer(authHeader)) {
                UsernamePasswordAuthenticationToken authentication = getUsernamePasswordAuthenticationToken(authHeader);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        } catch (TokenExpiredException ex) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
        }

    }

    private UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(String authHeader) {
        int codRol = jwtService.idRol(authHeader);
        List<GrantedAuthority> authorities = Stream.of(codRol)
                .map(x -> new SimpleGrantedAuthority(ROLE_PREFIX + x))
                .collect(Collectors.toList());
        UserSessionDto user = new UserSessionDto(
                jwtService.id(authHeader),
                jwtService.nombres(authHeader),
                jwtService.idRol(authHeader),
                jwtService.codRed(authHeader),
                jwtService.codUnidad(authHeader),
                jwtService.idRolAdicional(authHeader));
        return new UsernamePasswordAuthenticationToken(user, null, authorities);
    }

}