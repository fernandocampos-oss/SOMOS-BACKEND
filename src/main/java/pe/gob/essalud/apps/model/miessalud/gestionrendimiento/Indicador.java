package pe.gob.essalud.apps.model.miessalud.gestionrendimiento;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "indicador")
public class Indicador {

    @Id
    @Column(name = "id_indicador")
    private Integer idIndicador;

    @Column(name="descripcion", nullable = false, length = 250)
    private String descripcion;

    @Column(name = "estado", nullable = true)
    private boolean estado;

}
