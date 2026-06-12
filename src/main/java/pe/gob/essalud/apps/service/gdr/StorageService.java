package pe.gob.essalud.apps.service.gdr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import pe.gob.essalud.apps.dto.storage.StorageDownloadResponseDto;
import pe.gob.essalud.apps.dto.storage.StorageUploadResponseDto;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StorageService {

    private final RestTemplate storageRestTemplate;

    @Value("${storage.url:}")
    private String storageUrl;

    @Value("${storage.api-key:}")
    private String storageApiKey;

    @Value("${storage.app-name:}")
    private String storageAppName;

    public StorageService(@Qualifier("storageRestTemplate") RestTemplate storageRestTemplate) {
        this.storageRestTemplate = storageRestTemplate;
    }

    /**
     * Sube un archivo al file server.
     * El identifier usa guiones (permitidos por el sanitizador del file server).
     * @return newFilename retornado por el file server
     */
    public String upload(byte[] fileBytes, String extension, int idEvidencia) {
        String identifier = "GDR-EVIDENCIA-" + idEvidencia;
        String fileName = identifier + "." + extension;

        ByteArrayResource fileResource = new ByteArrayResource(fileBytes) {
            @Override
            public String getFilename() {
                return fileName;
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileResource);
        body.add("nameApp", storageAppName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("api_key", storageApiKey);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        log.info("[StorageService] Subiendo archivo al file server: identifier={}, extension={}", identifier, extension);

        ResponseEntity<StorageUploadResponseDto> response = storageRestTemplate.exchange(
            storageUrl + "/upload",
            HttpMethod.POST,
            requestEntity,
            StorageUploadResponseDto.class
        );

        String newFilename = response.getBody() != null ? response.getBody().getNewFilename() : null;
        if (newFilename == null || newFilename.isBlank()) {
            throw new RuntimeException("El file server no devolvió un nombre de archivo válido");
        }

        log.info("[StorageService] Archivo subido exitosamente: {}", newFilename);
        return newFilename;
    }

    /**
     * Descarga un archivo del file server y lo retorna como base64.
     * Convierte el Buffer de Node.js (array de enteros) a byte[] y luego a base64.
     */
    public String download(String newFilename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api_key", storageApiKey);

        Map<String, String> body = new HashMap<>();
        body.put("nameFile", newFilename);
        body.put("nameApp", storageAppName);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

        log.info("[StorageService] Descargando archivo del file server: {}", newFilename);

        ResponseEntity<StorageDownloadResponseDto> response = storageRestTemplate.exchange(
            storageUrl + "/download",
            HttpMethod.POST,
            requestEntity,
            StorageDownloadResponseDto.class
        );

        if (response.getBody() == null || response.getBody().getFileBuffer() == null) {
            log.warn("[StorageService] El file server no devolvió datos para: {}", newFilename);
            return "";
        }

        List<Integer> dataInts = response.getBody().getFileBuffer().getData();
        if (dataInts == null || dataInts.isEmpty()) {
            return "";
        }

        // Convertir array de enteros (0-255) del Buffer de Node.js a byte[]
        byte[] fileBytes = new byte[dataInts.size()];
        for (int i = 0; i < dataInts.size(); i++) {
            fileBytes[i] = dataInts.get(i).byteValue();
        }

        log.info("[StorageService] Archivo descargado exitosamente: {}", newFilename);
        return Base64.getEncoder().encodeToString(fileBytes);
    }

    /**
     * Verifica la conectividad con el file server.
     * Si el servidor responde (aunque sea con 4xx/5xx), está disponible.
     * Solo falla si no hay conexión (timeout, refused, etc.).
     */
    public boolean healthCheck() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api_key", storageApiKey);

            Map<String, String> body = new HashMap<>();
            body.put("nameFile", "health-check-probe.txt");
            body.put("nameApp", storageAppName);

            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

            storageRestTemplate.exchange(
                storageUrl + "/download",
                HttpMethod.POST,
                requestEntity,
                String.class
            );
            return true;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // 4xx / 5xx: el servidor responde → está disponible
            return true;
        } catch (ResourceAccessException e) {
            // Timeout o conexión rechazada → servidor no disponible
            log.warn("[StorageService] Health check falló: servidor no accesible. {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.warn("[StorageService] Health check falló: {}", e.getMessage());
            return false;
        }
    }
}
