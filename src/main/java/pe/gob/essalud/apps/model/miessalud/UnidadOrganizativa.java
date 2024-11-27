package pe.gob.essalud.apps.model.miessalud;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "unidad_organizativa")
public class UnidadOrganizativa {

    @Id
    @Column(name = "cod_unidad")
    private String codUnidad;
    private String descripcion;
    @Column(name = "cod_padre")
    private String codPadre;
}
