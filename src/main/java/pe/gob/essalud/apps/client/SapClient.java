package pe.gob.essalud.apps.client;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Log4j2
@RequiredArgsConstructor
public class SapClient {

    private final Authentication authentication;

    @Builder
    public static class Authentication{
        private String urlWs;
        private String usuarioWs;
        private String claveWs;
    }

    public <T> T getData(String xmlInput, String keyDataExtract, Class<T> classToBound) {
        T respuestaSAP = null;
        try {

            String responseString;
            URL url = new URL(authentication.urlWs);
            URLConnection connection = url.openConnection();
            String userpassword = authentication.usuarioWs + ":" + authentication.claveWs;
            String encodedAuthorization = Base64.getEncoder().encodeToString(userpassword.getBytes(StandardCharsets.UTF_8));
            HttpURLConnection httpConn = (HttpURLConnection) connection;
            ByteArrayOutputStream bout = new ByteArrayOutputStream();

            byte[] buffer;
            buffer = xmlInput.getBytes();
            bout.write(buffer);
            byte[] b = bout.toByteArray();
            httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
            httpConn.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
            httpConn.setRequestProperty("Authorization", "Basic " + encodedAuthorization);
           // httpConn.setRequestProperty("SOAP Version", "SOAP 1.1");
            httpConn.setRequestMethod("POST");
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            OutputStream out = httpConn.getOutputStream();
            out.write(b);
            out.close();
            InputStreamReader isr;
            if (httpConn.getResponseCode() == 200) {
                isr = new InputStreamReader(httpConn.getInputStream());
            } else {
                isr = new InputStreamReader(httpConn.getErrorStream());
            }
            BufferedReader in = new BufferedReader(isr);
            StringBuilder outputString = new StringBuilder();
            while ((responseString = in.readLine()) != null) {
                outputString.append(responseString);
            }
            String otLista = extractValue(outputString.toString(), keyDataExtract);
            if (otLista.equals("")) {
                return null;
            }
            if (classToBound.isAssignableFrom(String.class)) {
                respuestaSAP = (T) otLista;
            } else {
                otLista = "<" + keyDataExtract + ">".concat(otLista).concat("</" + keyDataExtract + ">");
                StringReader sr = new StringReader(otLista);
                JAXBContext jaxbContext = JAXBContext.newInstance(classToBound);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                respuestaSAP = (T) unmarshaller.unmarshal(sr);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().equals("Connection timed out: connect")) {
                throw new IllegalArgumentException("No se puede conectar el servicio SAP");
            } else {
                String message = "Error al consultar el servicio SAP";
                log.error(message, e);
                throw new IllegalArgumentException(message);
            }
        }
        return respuestaSAP;
    }

    private String extractValue(String xml, String key) {
        String value = "";
        if (!StringUtils.hasText(xml))
            return value;

        int indexInicio = xml.indexOf("<" + key + ">");
        int indexfin = xml.indexOf("</" + key + ">");
        if (indexfin > indexInicio)
            value = xml.substring(indexInicio, indexfin);

        return value.replace("<" + key + ">", "");
    }

}
