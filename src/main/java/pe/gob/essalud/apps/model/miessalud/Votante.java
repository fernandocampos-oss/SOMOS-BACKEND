package pe.gob.essalud.apps.model.miessalud;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "votante")
public class Votante {

    @Id
    @Column(name = "id_votante")
    private Integer idVotante;
    @Column(name = "numero_documento")
    private String numeroDocumento;
    @Column(name = "nombres")
    private String nombres;
    @Column(name = "apellidos")
    private String apellidos;
    @Column(name = "id_segmento")
    private Integer idSegmento;

}