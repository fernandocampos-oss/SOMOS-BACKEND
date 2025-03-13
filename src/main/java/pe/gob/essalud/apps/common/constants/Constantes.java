package pe.gob.essalud.apps.common.constants;

public final class Constantes {

    private Constantes() {
    }

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_XLSX = "application/xls; charset=UTF-8";
    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    public static final String ATTACHMENT = "attachment; filename=\"";

    public static final String URL_REDIRECT_RECUPERAR_CLAVE = "redirect.recuperar-clave";
    public static final String URL_REDIRECT_SALUDO_ONOMASTICO = "redirect.saludo-onomastico";

    public static final String FORMATO_FECHA_LARGA = "yyyy-MM-dd HH:mm";
    public static final String FORMATO_HORA_MIN = " 00:00";
    public static final String ERROR_KEY_PROPERTIES = "Error en valor de properties.";

    // CAPTCHA
    public static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
}
