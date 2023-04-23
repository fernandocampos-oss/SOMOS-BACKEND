package com.marcas.common.annotations;

import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAnyRole(T(com.marcas.common.constants.RoleType).ADMIN_CENTRAL, T(com.marcas.common.constants.RoleType).ADMIN_SEDE)")
public @interface PreAuthorizeAdmin {
}
