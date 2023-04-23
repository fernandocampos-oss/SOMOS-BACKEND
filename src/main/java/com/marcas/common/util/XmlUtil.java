package com.marcas.common.util;

import lombok.Builder;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Builder
public class XmlUtil {
    public static String replaceVariable(String data, String key, String value) {
        return data.replace("${" + key + "}", value);
    }

    public static String convertToString(Resource resource) throws IOException {
        return convertToString(resource.getInputStream());
    }

    public static String convertToString(InputStream file) throws IOException {
        BufferedReader bufReader = new BufferedReader(new InputStreamReader(file));
        StringBuilder sb = new StringBuilder();
        String line = bufReader.readLine();
        while (line != null) {
            sb.append(line).append("\n");
            line = bufReader.readLine();
        }
        bufReader.close();
        return sb.toString();
    }
}
