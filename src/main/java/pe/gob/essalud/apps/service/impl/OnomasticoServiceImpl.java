package pe.gob.essalud.apps.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriComponentsBuilder;
import pe.gob.essalud.apps.base.BaseService;
import pe.gob.essalud.apps.common.constants.Constantes;
import pe.gob.essalud.apps.dto.onomastico.response.OnomasticoResponseDto;
import pe.gob.essalud.apps.model.miessalud.Onomastico;
import pe.gob.essalud.apps.model.miessalud.Usuario;
import pe.gob.essalud.apps.repository.miessalud.OnomasticoRepository;
import pe.gob.essalud.apps.service.OnomasticoService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OnomasticoServiceImpl extends BaseService implements OnomasticoService {

    private final OnomasticoRepository onomasticoRepository;

//    @Value("${feign-clients.email-somos-service}")
//    private String urlEmail;

    @Override
    public List<Onomastico> findAllOnomasticos() {
        return onomasticoRepository.findAll();
    }

    @Override
    public List<Onomastico> findAllOnomasticosByMes(String mes) {
        return onomasticoRepository.findByMes(mes);
    }

    @Override
    public List<Onomastico> findAllOnomasticosByMesAndDia(String mes, String dia) {
        return onomasticoRepository.findByMesAndDia(mes, dia);
    }

    @Override
    public Usuario findUsuarioByNumDocAndEstado(String numeroDocumento) {
        return onomasticoRepository.findUsuarioByNumDocAndEstado(numeroDocumento);
    }

    @Override
    public List<OnomasticoResponseDto> obtenerOnomasticosPorDiaAndEstado(String mes, String dia) {
        List<OnomasticoResponseDto> listOnomastico = new ArrayList<>();
        List<Object[]> listUser =  onomasticoRepository.obtenerOnomasticosPorDiaAndEstado(mes, dia);
        log.info("Total correos a enviar: [{}]", listUser.size());
        for (Object[] row : listUser) {
            OnomasticoResponseDto trabajador = new OnomasticoResponseDto();
            String nombre = (String) row[1];
            String apellido = (String) row[2];
            String correo = (String) row[4];
            trabajador.setNombres(nombre);
            trabajador.setApellidos(apellido);
            trabajador.setCorreo(correo);
            listOnomastico.add(trabajador);
            log.info("Nombre: [{}], Apellido: [{}], Correo: [{}]", (String) row[1], (String) row[2], (String) row[4]);
            String nombreCompleto = trabajador.getNombres() + " " + trabajador.getApellidos();
            _sendMailSaludoOnomastico(correo, nombreCompleto);
        }
        return listOnomastico;
    }

    @Async
    protected void _sendMailSaludoOnomastico(String correo, String nombreCompleto) {
        log.info("correo a notificar: [{}-{}]", correo, nombreCompleto);
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(5000);
        RestTemplate restTemplate = new RestTemplate(requestFactory);

        String url = getProperty(Constantes.URL_REDIRECT_SALUDO_ONOMASTICO);
        url = UriComponentsBuilder.fromUriString(url).build().encode().toUriString();

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", correo);
        requestBody.put("nombre", nombreCompleto);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            log.info("Código de respuesta: [{}], Cuerpo de la respuesta: [{}]", response.getStatusCode(), response.getBody());
        } catch (Exception e) {
            log.info("Error en la petición: [{}]", e.getMessage());
        }
    }

}
