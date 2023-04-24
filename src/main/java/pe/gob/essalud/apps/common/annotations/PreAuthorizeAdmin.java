package pe.gob.essalud.apps.common.annotations;

import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAnyRole(T(pe.gob.essalud.apps.common.constants.RoleType).ADMIN_CENTRAL, T(pe.gob.essalud.apps.common.constants.RoleType).ADMIN_SEDE)")
public @interface PreAuthorizeAdmin {
}
