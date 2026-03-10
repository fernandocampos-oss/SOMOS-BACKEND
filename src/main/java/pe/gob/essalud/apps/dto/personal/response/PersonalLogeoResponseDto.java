package pe.gob.essalud.apps.dto.personal.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonalLogeoResponseDto {
    private boolean success;
    private String mensaje;
    
    // Datos Personales
    private String nombres;
    private String apellidos;
    private String dni;
    private String correo;
    private String fechaNacimiento;      // Fecha de nacimiento
    private String numeroCelular;        // Teléfono móvil
    private String sexo;
    
    // Datos Laborales
    private String codigoPlanilla;
    private String cargo;                // Cargo actual
    private String regimen;              // Régimen Laboral
    private String fechaIngreso;         // Fecha de ingreso
    private String dependencia;          // Red Prestacional (descripción)
    private String lugarLabora;          // Unidad donde labora (descripción)
    
    // Datos de SAP (pueden ser null si no se encuentra)
    private String numeroPlaza;          // Número de Plaza
    private String nivelCargo;           // Nivel del cargo
}
