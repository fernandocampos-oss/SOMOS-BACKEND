package pe.gob.essalud.apps.filters;

import org.springframework.web.filter.OncePerRequestFilter;
import pe.gob.essalud.apps.service.RecaptchaEnterpriseService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RecaptchaValidationFilter extends OncePerRequestFilter {

    private final RecaptchaEnterpriseService recaptchaEnterpriseService;

    public RecaptchaValidationFilter(RecaptchaEnterpriseService recaptchaEnterpriseService) {
        this.recaptchaEnterpriseService = recaptchaEnterpriseService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath  = request.getServletPath();
        if (("/auth".equals(requestPath ) || "/auth/login".equals(requestPath )) && "POST".equalsIgnoreCase(request.getMethod())) {
            String captchaToken = request.getHeader("X-Captcha-Token");
            String captchaAction = request.getHeader("X-Captcha-Action");
            if (captchaToken == null || !recaptchaEnterpriseService.verifyToken(captchaToken, captchaAction)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid captcha");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}

