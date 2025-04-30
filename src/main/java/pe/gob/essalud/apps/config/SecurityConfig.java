package pe.gob.essalud.apps.config;

import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import pe.gob.essalud.apps.filters.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.gob.essalud.apps.filters.RecaptchaValidationFilter;
import pe.gob.essalud.apps.service.RecaptchaEnterpriseService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final RecaptchaEnterpriseService recaptchaEnterpriseService;

    public SecurityConfig(@Qualifier("app.users") UserDetailsService userDetailsService,
                          PasswordEncoder passwordEncoder, RecaptchaEnterpriseService recaptchaEnterpriseService) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.recaptchaEnterpriseService = recaptchaEnterpriseService;
    }

    @Override
    @Autowired
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable() //Cross-Site Request Forgery disable to API
            .httpBasic() // login with Auth Basic for getting a login
            .authenticationEntryPoint(new Http401AuthenticationEntryPoint())
            .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // stateless to API
            .and()
            .addFilterBefore(new RecaptchaValidationFilter(recaptchaEnterpriseService), BasicAuthenticationFilter.class)
            .addFilter(jwtAuthorizationFilter());
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() throws Exception {
        return new JwtAuthorizationFilter(this.authenticationManager());
    }

}