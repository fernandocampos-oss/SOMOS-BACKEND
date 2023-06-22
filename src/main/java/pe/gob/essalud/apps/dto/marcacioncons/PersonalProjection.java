package pe.gob.essalud.apps.dto.marcacioncons;

import lombok.Data;

@Data
public class PersonalProjection {
	private String codplanilla;
	private String nombrepersonal;
	private String dni;
	private String fecha;
	private String hora;
	private String terminalmarca;
	private String descripcion;
	private String lectora;
	private String sede;
}
