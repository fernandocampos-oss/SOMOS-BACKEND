package com.marcas.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import java.io.Serializable;

public class BaseObject implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(BaseObject.class);

    @Autowired
    private Environment env;

    @Value("${spring.logging.info}")
    private String logginInfo;

    public String getProperty(String key) {
        return env.getProperty(key);
    }

    public void loggerException(String title, Exception e) {
        logger.error(title, e);
    }

    public void loggerInfo(String title, String info) {
        if (logginInfo.contains("show"))
            logger.info(title.concat(":").concat(info));
    }

}
