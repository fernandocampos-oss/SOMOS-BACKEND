package pe.gob.essalud.apps.model.miessalud;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "candidato")
public class Candidato {

    @Id
    @Column(name = "id_candidato")
    private Integer idCandidato;
    @Column(name = "numero_documento")
    private String numeroDocumento;
    @Column(name = "nombres")
    private String nombres;
    @Column(name = "apellidos")
    private String apellidos;
    @Column(name = "id_segmento")
    private Integer idSegmento;

}