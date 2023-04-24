package pe.gob.essalud.apps.common.email;

public enum EmailTemplate {

    ACTIVATION_TOKEN("activation_token"),
    RESET_PASSWORD("reset_password");

    public final String value;

    EmailTemplate(String value) {
        this.value = value;
    }

}
