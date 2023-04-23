package com.marcas.dto.auth.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AuthUsuarioRegisterRequestDto {

    private String numeroDocumento;
    private String codigoPlanilla;
    private LocalDate fechaNacimiento;
    private String numeroCelular;
    private String correo;
    private String password;
    private int idSede;
    private String idUbigeo;
    private String direccion;
    private String numeroTelefono;

}
