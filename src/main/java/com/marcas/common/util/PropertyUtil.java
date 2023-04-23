package com.marcas.common.util;

import com.marcas.common.constants.Constantes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class PropertyUtil {

    private static final Logger logger = LogManager.getLogger(PropertiesUtil.class);
    private final Environment env;

    public PropertyUtil(Environment env) {
        this.env = env;
    }

    public String getPropertiesString(String key) {
        return getValueEncoding(env.getProperty(key));
    }

    private String getValueEncoding(String value) {
        String val = "";
        try {
            if (StringUtils.isEmpty(value)) {
                throw new UnsupportedEncodingException(Constantes.ERROR_KEY_PROPERTIES);
            }
            return new String(value.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        } catch (UnsupportedEncodingException e) {
            logger.info(e);
        }
        return val;
    }

}
