package com.marcas.client.personalsap;

import com.marcas.client.SapClient;
import com.marcas.client.personalsap.model.PersonaSAP;
import com.marcas.client.personalsap.model.RespuestaPersonaSap;
import com.marcas.common.util.XmlUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;

@Component
public class PersonalSapClient {

    @Value("classpath:xml/persona-sap-por-fecha-nacimiento-request.xml")
    Resource personalSapRequestXml;

    private final SapClient sapClient;

    private final static String KEY_NUMERO_DOCUMENTO = "NUMERO_DOCUMENTO";
    private final static String KEY_FECHA_NACIMIENTO = "FECHA_NACIMIENTO";
    private final static String KEY_DATA = "OT_DATA";

    public PersonalSapClient(@Value("${ws.personal-sap.url}") String urlWs,
                             @Value("${ws.personal-sap.user}") String usuarioWs,
                             @Value("${ws.personal-sap.password}") String claveWs) {
        var authentication = SapClient.Authentication.builder()
                .urlWs(urlWs)
                .usuarioWs(usuarioWs)
                .claveWs(claveWs)
                .build();
        this.sapClient = new SapClient(authentication);
    }

    @SneakyThrows
    public PersonaSAP getPorNumeroDocumentoAndFechaNacimiento(String numeroDocumento, LocalDate fechaNacimiento) {

        String xmlInput = XmlUtil.convertToString(personalSapRequestXml);
        xmlInput = XmlUtil.replaceVariable(xmlInput, KEY_NUMERO_DOCUMENTO, numeroDocumento);
        xmlInput = XmlUtil.replaceVariable(xmlInput, KEY_FECHA_NACIMIENTO, fechaNacimiento.toString());

        RespuestaPersonaSap response = sapClient.getData(xmlInput, KEY_DATA, RespuestaPersonaSap.class);

        if (response == null || CollectionUtils.isEmpty(response.getData()))
            return null;

        return response.getData().get(0);
    }
}
