package pe.gob.essalud.apps.dto.gestionrendimiento.response;

public class VotantePlanillaResponseDto {
	
	public VotantePlanillaResponseDto(Integer idVotante, String numeroDocumento, String nombres, String apellidos,
			Integer idSegmento, Integer idUsuario, String codCondicion, String codigoPlanilla) {
		this.idVotante = idVotante;
		this.numeroDocumento = numeroDocumento;
		this.nombres = nombres;
		this.apellidos = apellidos;
		this.idSegmento = idSegmento;
		this.idUsuario = idUsuario;
		this.codCondicion = codCondicion;
		this.codigoPlanilla = codigoPlanilla;
	}
	private Integer idVotante;
	public Integer getIdVotante() {
		return idVotante;
	}
	public void setIdVotante(Integer idVotante) {
		this.idVotante = idVotante;
	}
	public String getNumeroDocumento() {
		return numeroDocumento;
	}
	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}
	public String getNombres() {
		return nombres;
	}
	public void setNombres(String nombres) {
		this.nombres = nombres;
	}
	public String getApellidos() {
		return apellidos;
	}
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	public Integer getIdSegmento() {
		return idSegmento;
	}
	public void setIdSegmento(Integer idSegmento) {
		this.idSegmento = idSegmento;
	}
	public Integer getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}
	public String getCodCondicion() {
		return codCondicion;
	}
	public void setCodCondicion(String codCondicion) {
		this.codCondicion = codCondicion;
	}
	public String getCodigoPlanilla() {
		return codigoPlanilla;
	}
	public void setCodigoPlanilla(String codigoPlanilla) {
		this.codigoPlanilla = codigoPlanilla;
	}
	private String numeroDocumento;
	private String nombres;
	private String apellidos;
	private Integer idSegmento;
	private Integer idUsuario;
	private String codCondicion;
	private String codigoPlanilla;
}
