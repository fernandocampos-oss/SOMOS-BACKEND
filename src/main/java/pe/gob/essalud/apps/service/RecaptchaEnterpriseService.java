package pe.gob.essalud.apps.service;

public interface RecaptchaEnterpriseService {

    boolean verifyToken(String token, String action, String clientIp);

}

