package com.marcas.dto.auth.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class GenerarTokenRecuperarClaveRequestDto {

    @NotEmpty(message = "El número de documento es obligatorio")
    private String numeroDocumento;

}
