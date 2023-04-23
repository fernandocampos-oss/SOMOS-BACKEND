package com.marcas.common.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class EmailContentBuilder {
    private static final String FRAGMENT = "fragment";
    private static final String TEMPLATE = "templates/email/template";
    private static final String FRAGMENTS_PATH = "templates/email/fragments/";
    private final TemplateEngine templateEngine;

    @Autowired
    public EmailContentBuilder(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String registrationCode(String code) {
        Context context = getContext(EmailTemplate.ACTIVATION_TOKEN);
        context.setVariable("code", code);

        return build(context);
    }

    private Context getContext(EmailTemplate emailTemplate) {
        Context context = new Context();
        String fragment = FRAGMENTS_PATH + emailTemplate.value;
        context.setVariable(FRAGMENT, fragment);
        return context;
    }

    private String build(Context context) {
        return templateEngine.process(TEMPLATE, context);
    }

    public String resetPassword(String url) {
        Context context = getContext(EmailTemplate.RESET_PASSWORD);
        context.setVariable("url", url);

        return build(context);
    }
}