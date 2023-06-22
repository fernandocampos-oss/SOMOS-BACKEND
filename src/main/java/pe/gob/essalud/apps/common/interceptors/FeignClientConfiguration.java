package pe.gob.essalud.apps.common.interceptors;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pe.gob.essalud.apps.common.interfaces.BasicAuthForMarcacionConsService;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class FeignClientConfiguration {

    @Value("${feign-clients.marcacion-cons-service.key}")
    private String marcacionConsServiceKey;


    private void _addHeader(RequestTemplate template, Class<? extends java.lang.annotation.Annotation> interfaceClass, String key) {
        if (template.feignTarget() != null && template.feignTarget().type().isAnnotationPresent(interfaceClass)) {
            String authHeader = "Basic " + Base64.getEncoder().encodeToString((key).getBytes(StandardCharsets.UTF_8));
            template.header("Authorization", authHeader);
        }
    }

    @Bean
    public RequestInterceptor basicAuthRequestInterceptor() {
        return template -> {
            _addHeader(template, BasicAuthForMarcacionConsService.class, marcacionConsServiceKey);
        };
    }
}