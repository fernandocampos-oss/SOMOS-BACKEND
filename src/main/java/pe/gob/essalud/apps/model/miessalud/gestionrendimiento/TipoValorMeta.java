package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "tipo_valor_meta")
public class TipoValorMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_valor_meta")
    private Integer idTipoValorMeta;

    @Column(name="codigo")
    private String codigo;

    @Column(name="descripcion")
    private String descripcion;

    @Column(name = "estado")
    private boolean estado;

}
