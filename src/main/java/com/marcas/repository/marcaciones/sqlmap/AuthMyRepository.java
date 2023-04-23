package com.marcas.repository.marcaciones.sqlmap;

import com.marcas.dto.auth.UserSessionDto;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthMyRepository {

    UserSessionDto findByUsername(String username);

}
