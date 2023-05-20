package pe.gob.essalud.apps.common.annotations;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@PreAuthorize("hasAnyRole(T(pe.gob.essalud.apps.common.constants.RoleType).ADMIN_CENTRAL, T(pe.gob.essalud.apps.common.constants.RoleType).ADMIN_SEDE)")
public @interface PreAuthorizeAdmin {
}
